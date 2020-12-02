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
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_post_details.*
import kotlinx.android.synthetic.main.post_details.*
import ru.krivonosovdenis.fintechapp.BuildConfig
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.interfaces.PostDetailsActions
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.MvpFragment
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import ru.krivonosovdenis.fintechapp.rvcomponents.PostDetailsRVAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PostDetailsFragment : MvpFragment<PostDetailsView, PostDetailsPresenter>(), PostDetailsView,
    SwipeRefreshLayout.OnRefreshListener, PostDetailsActions {

    private lateinit var rvAdapter: PostDetailsRVAdapter
    private lateinit var postId: Pair<Int, Int>

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
        postDetailsAndCommentsSwipeRefreshLayout.setOnRefreshListener(this)
        (activity as MainActivity).postsBottomNavigation.isGone = true

        rvAdapter = PostDetailsRVAdapter(this)
        with(postDetailsAndCommentsView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
        }

        postId = arguments!!.getInt(POST_ID) to arguments!!.getInt(SOURCE_ID)
        getPresenter().loadPostCommentsFromApiAndInsertIntoDB(postId.first, postId.second)
        getPresenter().subscribeOnPostDetailsFromDb(postId.first, postId.second)
        getPresenter().subscribeOnPostCommentsFromDb(postId.first, postId.second)
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


    private fun checkExternalWritePermission() {
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
                R.string.grant_permissions_check_alert_dialog_negative_button_text
            ) { dialog, _ -> dialog.cancel() }
            .setPositiveButton(
                R.string.grant_permissions_check_alert_dialog_positive_button_text
            ) { _, _ ->
                checkExternalWritePermission()
            }
            .create().show()
    }


    override fun renderPostDetails(postDetailsData: PostFullData) {
        val finalData = mutableListOf<InfoRepresentationClass>()
        val adapterData = rvAdapter.dataUnits
        adapterData.removeAll{it is PostFullData }
        finalData.add(postDetailsData)
        finalData.addAll(adapterData)
        rvAdapter.dataUnits = finalData

        postDetailsAndCommentsView.isVisible = true
        loadingView.isGone = true
        dbLoadingErrorView.isGone = true
    }

    override fun renderPostComments(comments: List<CommentData>) {
        val finalData = mutableListOf<InfoRepresentationClass>()
        val adapterData = rvAdapter.dataUnits
        val postDetailsData = adapterData.find { it is PostFullData }
        if(postDetailsData!=null){
            finalData.add(postDetailsData)
        }
        finalData.addAll(comments)
        rvAdapter.dataUnits = finalData

        postDetailsAndCommentsView.isVisible = true
        loadingView.isGone = true
        dbLoadingErrorView.isGone = true
    }

    override fun showDbLoadingErrorView() {
        postDetailsAndCommentsView.isGone = true
        dbLoadingErrorView.isVisible = true
    }

    override fun showPostView() {
        postDetailsAndCommentsView.isVisible = true
        dbLoadingErrorView.isGone = true
    }



    override fun setRefreshing(isRefreshing: Boolean) {
        postDetailsAndCommentsSwipeRefreshLayout.isRefreshing = isRefreshing
    }

    override fun showLoadDataFromNetworkErrorView() {
        TODO("Not yet implemented")
    }

    override fun onRefresh() {
        getPresenter().loadPostCommentsFromApiAndInsertIntoDB(postId.first, postId.second)
    }

    override fun sharePostImage(post: PostFullData) {
        shareImageIntent(postImage)
    }

    override fun savePostImage(post: PostFullData) {
        checkExternalWritePermission()
    }

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
}
