package ru.krivonosovdenis.fintechapp.presentation.sendpost

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_send_post.*
import kotlinx.android.synthetic.main.post_details.*
import kotlinx.android.synthetic.main.send_post_toolbar.*
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.MvpFragment
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity


class SendPostFragment : MvpFragment<SendPostView, SendPostPresenter>(), SendPostView {
    private val compositeDisposable = CompositeDisposable()

    private val imagesArray = ArrayList<String>()

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(s==null ||  s.count()==0 ||s=="" || s.trim{it <= ' '}.replace("\\s".toRegex(),"")=="" ){
                Log.e("zerozero","1233");
                sendPost.isClickable = false
                sendPost.setBackgroundResource(R.drawable.send_post_done_inactive_icon)
                return
            }
            else {
                Log.e("zerozero","12334");
                sendPost.isClickable = true
                sendPost.setBackgroundResource(R.drawable.send_post_done_active_icon)
            }
        }
    }

    override fun getPresenter(): SendPostPresenter = GlobalDI.INSTANCE.sendPostPresenter

    override fun getMvpView(): SendPostView = this

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_send_post, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).postsBottomNavigation.isGone = true
        val vkUserId = arguments!!.getInt(VK_USER_ID)
        getPresenter().getUserDataFromDb(vkUserId)
    }


    override fun showSendPostSuccessView() {
        sendPost.isClickable = false
        Toast.makeText(requireContext(), resources.getString(R.string.send_post_to_wall_success), Toast.LENGTH_LONG).show()
    }

    override fun showSendPostErrorView() {
        Toast.makeText(requireContext(), resources.getString(R.string.send_post_to_wall_error), Toast.LENGTH_LONG).show()
    }

    override fun showSendPostEditorView(userData: UserProfileMainInfo) {
        Glide.with(activity as MainActivity)
            .load(userData.photo)
            .centerCrop()
            .into(userAvatar)
        toolbarBackButton.setOnClickListener{
            (activity as MainActivity).onBackPressed()
        }
        userName.text = "${userData.firstName} ${userData.lastName}"
        sendPost.setOnClickListener {
            getPresenter().sendPostToBack(postMessageEditText.text.toString(),"")
        }
        addImagesToPost.setOnClickListener{
            checkReadStoragePermission()
        }
        postMessageEditText.addTextChangedListener(textWatcher)

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showGrantReadPermissionErrorAlert()

                } else {
                    choseImageFromGallery()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_FROM_GALLERY_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Log.e("ohoho", "here");
            val uri = data!!.data
//            val filePath = getRealPathFromURIPath(uri, this@SupportSendTicket)
//            filePathTv!!.text = filePath
//            filePathTv!!.visibility = View.VISIBLE
        }
    }

    private fun checkReadStoragePermission() {
        if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            choseImageFromGallery()
            return
        }
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_EXTERNAL_STORAGE_REQUEST_CODE
        )
    }

    private fun choseImageFromGallery(){
        val openGalleryIntent = Intent(Intent.ACTION_PICK)
        openGalleryIntent.type = "image/*"
        startActivityForResult(openGalleryIntent, REQUEST_IMAGE_FROM_GALLERY_CODE)
    }

    private fun showGrantReadPermissionErrorAlert() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle)
            .setTitle(getString(R.string.alert_dialog_error_title_text))
            .setMessage(getString(R.string.read_permissions_check_alert_dialog_message_text))
            .setCancelable(false)
            .setNegativeButton(
                R.string.grant_permissions_check_alert_dialog_negative_button_text
            ) { dialog, _ -> dialog.cancel() }
            .setPositiveButton(
                R.string.grant_permissions_check_alert_dialog_positive_button_text
            ) { _, _ ->
                checkReadStoragePermission()
            }
            .create().show()
    }

    override fun showSendPostEditorInitErrorView() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val VK_USER_ID = "vk_user_id"
        private const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 49
        private const val REQUEST_IMAGE_FROM_GALLERY_CODE = 23
        fun newInstance(userId: Int): SendPostFragment {
            return SendPostFragment().apply {
                arguments = Bundle().apply {
                    putInt(VK_USER_ID, userId)
                }
            }
        }
    }


}
