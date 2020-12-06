package ru.krivonosovdenis.fintechapp.ui.sendpost

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_send_post.*
import kotlinx.android.synthetic.main.send_post_toolbar.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.krivonosovdenis.fintechapp.ApplicationClass
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileData
import ru.krivonosovdenis.fintechapp.ui.mainactivity.MainActivity
import javax.inject.Inject

class SendPostFragment : MvpAppCompatFragment(), SendPostView {

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
                sendPost.isClickable = false
                sendPost.setBackgroundResource(R.drawable.send_post_done_inactive_icon)
                return
            } else {
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
        sendPost.isClickable = true
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
        sendPost.isClickable = false
        postMessageEditText.addTextChangedListener(textWatcher)
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
            sendPost.isClickable = false
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
        fun newInstance(userId: Int): SendPostFragment {
            return SendPostFragment().apply {
                arguments = Bundle().apply {
                    putInt(VK_USER_ID, userId)
                }
            }
        }
    }
}
