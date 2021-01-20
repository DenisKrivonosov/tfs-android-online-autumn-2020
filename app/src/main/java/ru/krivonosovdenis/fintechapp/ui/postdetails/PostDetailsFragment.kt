package ru.krivonosovdenis.fintechapp.ui.postdetails

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import kotlinx.android.synthetic.main.fragment_post_details.loadingView
import kotlinx.android.synthetic.main.post_details.*
import kotlinx.android.synthetic.main.send_post_toolbar.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.krivonosovdenis.fintechapp.ApplicationClass
import ru.krivonosovdenis.fintechapp.BuildConfig
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.interfaces.CommentsActions
import ru.krivonosovdenis.fintechapp.interfaces.PostDetailsActions
import ru.krivonosovdenis.fintechapp.ui.mainactivity.MainActivity
import ru.krivonosovdenis.fintechapp.rvcomponents.PostDetailsRVAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class PostDetailsFragment : MvpAppCompatFragment(), PostDetailsView,
    SwipeRefreshLayout.OnRefreshListener, PostDetailsActions, CommentsActions {

    private lateinit var rvAdapter: PostDetailsRVAdapter
    private lateinit var postId: Pair<Int, Int>

    @Inject
    @InjectPresenter
    lateinit var presenter: PostDetailsPresenter

    @ProvidePresenter
    fun provide() = presenter

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s == null || s.count() == 0 || s == "" || s.trim { it <= ' ' }
                    .replace("\\s".toRegex(), "") == "") {
                sendComment.isClickable= false
                sendComment.setBackgroundResource(R.drawable.send_comment_inactive_icon)
                return
            } else {
                sendComment.isClickable = true
                sendComment.setBackgroundResource(R.drawable.send_comment_active_icon)
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.applicationContext as ApplicationClass).addPostDetailsComponent()
        (activity?.applicationContext as ApplicationClass).postDetailsComponent?.inject(this)
        (activity as MainActivity).hideBottomSettings()
    }

    override fun onDetach() {
        (activity?.applicationContext as ApplicationClass).clearPostDetailsComponent()
        super.onDetach()
    }

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
        rvAdapter = PostDetailsRVAdapter(this, this)
        with(postDetailsAndCommentsRecycler) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
        }
        postId = requireArguments().getInt(POST_ID) to requireArguments().getInt(SOURCE_ID)
        commentInputET.addTextChangedListener(textWatcher)
        sendComment.setOnClickListener {
            sendComment()
        }
        sendComment.isClickable = false
        presenter.subscribeOnPostDetailsFromDb(postId.first, postId.second)
        presenter.subscribeOnPostCommentsFromDb(postId.first, postId.second)
        getPostDetailsFromApi()
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

    override fun renderPostDetails(postDetailsData: PostData) {
        val finalData = mutableListOf<InfoRepresentationClass>()
        val commentsData = rvAdapter.dataUnits.filterIsInstance<CommentData>()
        finalData.add(postDetailsData)
        finalData.addAll(commentsData)
        rvAdapter.dataUnits = finalData
        postDetailsAndCommentsView.isVisible = true
        loadingView.isGone = true
        dbLoadingErrorView.isGone = true
    }

    override fun renderPostComments(comments: List<CommentData>) {
        val finalData = mutableListOf<InfoRepresentationClass>()
        val adapterData = rvAdapter.dataUnits
        val postDetailsData = adapterData.find { it is PostData }
        if (postDetailsData != null) {
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
        dbLoadingErrorView.isVisible = true
        postDetailsAndCommentsView.isGone = true
        loadingView.isGone = true
    }

    override fun showPostUpdateErrorToast() {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.post_update_error_text),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showCommentUpdateErrorToast() {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.comment_update_error_text),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun sendCommentSuccessUpdateViewFromApi() {
        commentInputET.setText("")
        sendComment.isClickable = false
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.comment_send_success),
            Toast.LENGTH_SHORT
        ).show()
        getPostDetailsFromApi()
    }

    override fun showSendCommentError() {
        commentInputET.setText("")
        sendComment.isClickable = true
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.comment_send_error_text),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onRefresh() {
        getPostDetailsFromApi()
    }

    override fun sharePostImage(post: PostData) {
        shareImageIntent(postImage)
    }

    override fun savePostImage(post: PostData) {
        checkExternalWritePermission()
    }

    override fun likePost(post: PostData) {
        likePostOnApi(post)
    }

    override fun dislikePost(post: PostData) {
        dislikePostOnApi(post)
    }

    override fun likeComment(comment: CommentData) {
        likeCommentOnApi(comment)
    }

    override fun dislikeComment(comment: CommentData) {
        dislikeCommentOnApi(comment)
    }

    private fun sendComment(){
        if (!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable) {
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_can_not_perform_action))
        } else {
            sendComment.isClickable = false
            presenter.sendCommentToBack(commentInputET.text.toString(), postId.first, postId.second)
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
            Toast.LENGTH_SHORT
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

    private fun getPostDetailsFromApi(){
        if(!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable){
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_show_cached))
            postDetailsAndCommentsSwipeRefreshLayout.isRefreshing = false
        }
        else{
            presenter.loadPostCommentsFromApiAndInsertIntoDB(postId.first, postId.second)
        }
    }

    private fun likePostOnApi(post:PostData){
        if(!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable){
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_can_not_perform_action))
        }
        else{
            presenter.likePostOnApiAndDb(post)
        }
    }

    private fun dislikePostOnApi(post:PostData){
        if(!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable){
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_can_not_perform_action))
        }
        else{
            presenter.dislikePostOnApiAndDb(post)
        }
    }

    private fun likeCommentOnApi(comment: CommentData){
        if(!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable){
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_can_not_perform_action))
        }
        else{
            presenter.likeCommentOnApiAndDb(comment)
        }
    }

    private fun dislikeCommentOnApi(comment: CommentData){
        if(!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable){
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_can_not_perform_action))
        }
        else{
            presenter.dislikeCommentOnApiAndDb(comment)
        }
    }

    private fun networkIsNotAvailableMessage(toastText:String){
        Toast.makeText(
            requireContext(),
            toastText,
            Toast.LENGTH_SHORT
        ).show()
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
