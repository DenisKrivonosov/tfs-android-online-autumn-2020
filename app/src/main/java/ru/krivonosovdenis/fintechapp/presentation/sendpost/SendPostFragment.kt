package ru.krivonosovdenis.fintechapp.presentation.sendpost

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_send_post.*
import kotlinx.android.synthetic.main.send_post_toolbar.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.krivonosovdenis.fintechapp.ApplicationClass
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileData
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import javax.inject.Inject

class SendPostFragment : MvpAppCompatFragment(), SendPostView {
    private val imagesArray = ArrayList<String>()

    @Inject
    @InjectPresenter
    lateinit var presenter: SendPostPresenter

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
                Log.e("zerozero", "1233");
                sendPost.isClickable = false
                sendPost.setBackgroundResource(R.drawable.send_post_done_inactive_icon)
                return
            } else {
                Log.e("zerozero", "12334");
                sendPost.isClickable = true
                sendPost.setBackgroundResource(R.drawable.send_post_done_active_icon)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.applicationContext as ApplicationClass).addSendPostComponent()
        (activity?.applicationContext as ApplicationClass).sendPostComponent?.inject(this)
        (activity as MainActivity).hideBottomSettings()
        (activity as MainActivity).hideGlobalToolbar()
    }

    override fun onDetach() {
        (activity?.applicationContext as ApplicationClass).clearSendPostComponent()
        (activity as MainActivity).showGlobalToolbar()
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_send_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).postsBottomNavigation.isGone = true
        val vkUserId = requireArguments().getInt(VK_USER_ID)
        presenter.getUserDataFromDb(vkUserId)
    }

    override fun showSendPostSuccessView() {
        sendPost.isClickable = false
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.send_post_to_wall_success),
            Toast.LENGTH_SHORT
        ).show()
        (activity as MainActivity).showGlobalToolbar()
        (activity as MainActivity).showUserProfileFragment()
    }

    override fun showSendPostErrorView() {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.send_post_to_wall_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showSendPostEditorView(userData: UserProfileData) {
        Glide.with(activity as MainActivity)
            .load(userData.photo)
            .centerCrop()
            .into(userAvatar)
        toolbarBackButton.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        userName.text = "${userData.firstName} ${userData.lastName}"
        sendPost.setOnClickListener {
            sendPostOnApi()
        }
        addImagesToPost.setOnClickListener {
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
        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            choseImageFromGallery()
            return
        }
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_EXTERNAL_STORAGE_REQUEST_CODE
        )
    }

    private fun choseImageFromGallery() {
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
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.toolbar_loading_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun sendPostOnApi() {
        if (!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable) {
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_can_not_perform_action))
        } else {
            presenter.sendPostToBack(postMessageEditText.text.toString(), "")
        }
    }

    private fun networkIsNotAvailableMessage(toastText: String) {
        Toast.makeText(
            requireContext(),
            toastText,
            Toast.LENGTH_SHORT
        ).show()
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
