package ru.krivonosovdenis.fintechapp.fragments

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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.soc_network_post_details.*
import org.joda.time.LocalDate
import ru.krivonosovdenis.fintechapp.BuildConfig
import ru.krivonosovdenis.fintechapp.MainActivity
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.PostRenderData
import ru.krivonosovdenis.fintechapp.dbclasses.ApplicationDatabase
import ru.krivonosovdenis.fintechapp.utils.humanizePostDate
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PostDetailsFragment : Fragment() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var postId: Pair<Int, Int>

    companion object {
        private const val POST_ID = "post_id"
        private const val SOURCE_ID = "source_id"
        private const val REQUEST_WRITE_EXTERNAL_CODE = 1

        fun newInstance(postId: Int, sourceId: Int): PostDetailsFragment {
            return PostDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(POST_ID, postId)
                    putInt(SOURCE_ID, sourceId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_details, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).postsBottomNavigation.isGone = true
        postId = arguments!!.getInt(POST_ID) to arguments!!.getInt(SOURCE_ID)
        getPostFromDB()
    }

    private fun getPostFromDB() {
        compositeDisposable.add(
            ApplicationDatabase.getInstance(context!!)?.feedPostsDao()
                ?.getPostById(postId.first, postId.second)!!
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onSuccess = {
                        renderPostData(it)
                        if (it.photo != null && it.photo != "") {
                            postActionShare.isVisible = true
                            postActionShare.setOnClickListener {
                                shareImageIntent(
                                    postImage
                                )
                            }
                            postActionSave.isVisible = true
                            postActionSave.setOnClickListener {
                                checkExternalWrightPermission()
                            }
                        } else {
                            postActionShare.isVisible = false
                            postActionSave.isVisible = false
                        }
                    }, onError = {
                        showGetPostErrorDialog()
                    }
                )
        )
    }

    private fun renderPostData(post: PostRenderData) {
        Glide.with(activity as MainActivity)
            .load(post.groupAvatar)
            .centerCrop()
            .into(posterAvatar)
        posterName.text = post.groupName
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
                "share_image_" + System.currentTimeMillis() + ".png"
            )
            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()

            bmpUri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
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
            "title_" + System.currentTimeMillis(),
            "description_" + System.currentTimeMillis()
        )
        Toast.makeText(
            requireContext(),
            getString(R.string.image_saved_toast_text),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showGetPostErrorDialog() {
        AlertDialog.Builder(activity as MainActivity)
            .setTitle(getString(R.string.alert_dialog_error_title_text))
            .setMessage(getString(R.string.get_data_alert_dialog_message_text))
            .setCancelable(false)
            .setNegativeButton(
                R.string.get_data_alert_dialog_negative_button_text
            ) { _, _ -> (activity as MainActivity).finish() }
            .setPositiveButton(
                R.string.get_data_alert_dialog_positive_button_text
            ) { _, _ ->
                getPostFromDB()
            }
            .create().show()
    }

    private fun showImageShareErrorDialog() {
        AlertDialog.Builder(activity as MainActivity)
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
        AlertDialog.Builder(requireContext())
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
}
