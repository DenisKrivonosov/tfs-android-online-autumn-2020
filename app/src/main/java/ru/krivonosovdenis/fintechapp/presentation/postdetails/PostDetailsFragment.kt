package ru.krivonosovdenis.fintechapp.presentation.postdetails

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_post_details.*
import kotlinx.android.synthetic.main.soc_network_post_details.*
import org.joda.time.LocalDate
import ru.krivonosovdenis.fintechapp.BuildConfig
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.MvpFragment
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import ru.krivonosovdenis.fintechapp.utils.humanizePostDate
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PostDetailsFragment : MvpFragment<PostDetailsView, PostDetailsPresenter>(), PostDetailsView {

    private lateinit var postId: Pair<Int, Int>

    companion object {
        private const val POST_ID = "post_id"
        private const val SOURCE_ID = "source_id"
        private const val REQUEST_WRITE_EXTERNAL_CODE = 1
        private const val COMPRESSION_QUALITY = 90
        private const val IMAGE_FILE_EXTENSION = "png"
        private const val PROVIDER_POSTFIX = ".provider"
        private const val TITLE = "title_"
        private const val DESCRIPTION = "description_"
        private const val SHARE_IMAGE = "share_image_"

        fun newInstance(postId: Int, sourceId: Int): PostDetailsFragment {
            return PostDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(POST_ID, postId)
                    putInt(SOURCE_ID, sourceId)
                }
            }
        }
    }

    override fun getPresenter(): PostDetailsPresenter = GlobalDI.INSTANCE.postDetailsPresenter

    override fun getMvpView(): PostDetailsView = this

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).postsBottomNavigation.isGone = true
        postId = arguments!!.getInt(POST_ID) to arguments!!.getInt(SOURCE_ID)
        getPresenter().getPostFromDb(postId.first, postId.second)
    }


    private fun shareImageIntent(view: ImageView) {
        val bmpUri: Uri? = getLocalBitmapUri(view)
        if (bmpUri != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
            shareIntent.type = "image/*"
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        } else {
            showImageShareErrorDialog()
        }
    }

    private fun getLocalBitmapUri(imageView: ImageView): Uri? {
        val bmp = imageViewToBitmap(imageView)
        var bmpUri: Uri? = null
        try {
            val file = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                SHARE_IMAGE + System.currentTimeMillis() + IMAGE_FILE_EXTENSION
            )
            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, out)
            out.close()

            bmpUri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + PROVIDER_POSTFIX,
                file
            )
        } catch (e: IOException) {
            showImageShareErrorDialog()
        }
        return bmpUri
    }

    private fun imageViewToBitmap(imageView: ImageView): Bitmap? {
        val drawable = imageView.drawable
        return if (drawable is BitmapDrawable) {
            (imageView.drawable as BitmapDrawable).bitmap
        } else {
            return null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showGrantWritePermissionErrorAlert()

                } else {
                    saveImageToGallery(postImage)
                }
            }
        }
    }

    private fun checkExternalWrightPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                saveImageToGallery(postImage)
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_CODE
                )
            }
        } else {
            saveImageToGallery(postImage)
        }
    }

    private fun saveImageToGallery(view: ImageView) {
        //Пока разобрался как делать только с помощью этого метода. Он был задепрекейчен в API 29
        MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            imageViewToBitmap(view),
            TITLE + System.currentTimeMillis(),
            DESCRIPTION + System.currentTimeMillis()
        )
        Toast.makeText(
            requireContext(),
            getString(R.string.image_saved_toast_text),
            Toast.LENGTH_LONG
        ).show()
    }


    private fun showImageShareErrorDialog() {
        MaterialAlertDialogBuilder(activity as MainActivity)
            .setTitle(getString(R.string.alert_dialog_error_title_text))
            .setMessage(getString(R.string.share_image_error_alert_dialog_message_text))
            .setCancelable(true)
            .setPositiveButton(
                R.string.get_data_alert_dialog_positive_button_text
            ) { dialog, _ ->
                dialog.cancel()

            }
            .create().show()
    }

    private fun showGrantWritePermissionErrorAlert() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle)
            .setTitle(getString(R.string.alert_dialog_error_title_text))
            .setMessage(getString(R.string.write_permissions_check_alert_dialog_message_text))
            .setCancelable(false)
            .setNegativeButton(
                R.string.write_permissions_check_alert_dialog_negative_button_text
            ) { dialog, _ -> dialog.cancel() }
            .setPositiveButton(
                R.string.write_permissions_check_alert_dialog_positive_button_text
            ) { _, _ ->
                checkExternalWrightPermission()
            }
            .create().show()
    }


    override fun showPost(post: PostFullData) {
        postDetailView.isVisible = true
        errorView.isGone = true
        renderPostData(post)
    }

    override fun showErrorView() {
        postDetailView.isGone = true
        errorView.isVisible = true
    }

    override fun showPostView() {
        postDetailView.isVisible = true
        errorView.isGone = true
    }

    private fun renderPostData(post: PostFullData) {
        Glide.with(activity as MainActivity)
            .load(post.posterAvatar)
            .centerCrop()
            .into(posterAvatar)
        posterName.text = post.posterName
        postDate.text =
            humanizePostDate(LocalDate().toDateTimeAtCurrentTime().millis, post.date.millis)
        postText.text = post.text
        if (post.photo != null) {
            Glide.with(activity as MainActivity)
                .load(post.photo)
                .into(postImage)
        } else {
            postImage.isGone = true
            postActionShare.isGone = true
        }
        postActionLike.background = if (post.isLiked) {
            ResourcesCompat.getDrawable(
                requireContext().resources,
                R.drawable.post_like_button_clicked_icon,
                requireContext().theme
            )
        } else {
            ResourcesCompat.getDrawable(
                requireContext().resources,
                R.drawable.post_like_button_icon,
                requireContext().theme
            )
        }
        postLikesCounter.text = post.likesCount.toString()

        if (!post.photo.isNullOrEmpty()) {
            postActionShare.isVisible = true
            postActionShare.setOnClickListener {
                shareImageIntent(postImage)
            }
            postActionSave.isVisible = true
            postActionSave.setOnClickListener {
                checkExternalWrightPermission()
            }
        } else {
            postActionShare.isVisible = false
            postActionSave.isVisible = false
        }
    }
}
