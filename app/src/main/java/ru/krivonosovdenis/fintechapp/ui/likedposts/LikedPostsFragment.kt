package ru.krivonosovdenis.fintechapp.ui.likedposts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_liked_posts.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.krivonosovdenis.fintechapp.ApplicationClass
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.interfaces.CommonAdapterActions
import ru.krivonosovdenis.fintechapp.interfaces.LikedPostsActions
import ru.krivonosovdenis.fintechapp.ui.mainactivity.MainActivity
import ru.krivonosovdenis.fintechapp.rvcomponents.CommonRVAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsListItemDecoration
import javax.inject.Inject

class LikedPostsFragment : MvpAppCompatFragment(), LikedPostsView,
    CommonAdapterActions,
    LikedPostsActions {

    private lateinit var rvAdapter: CommonRVAdapter

    @Inject
    @InjectPresenter
    lateinit var presenter: LikedPostsPresenter

    @ProvidePresenter
    fun provide() = presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.applicationContext as ApplicationClass).addLikedPostsComponent()
        (activity?.applicationContext as ApplicationClass).likedPostsComponent?.inject(this)
        (activity as MainActivity).showBottomSettings()
    }

    override fun onDetach() {
        (activity?.applicationContext as ApplicationClass).clearLikedPostsComponent()
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_liked_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavigationTabs()
        rvAdapter = CommonRVAdapter(this)
        with(likedPostsView) {
            layoutManager = LinearLayoutManager(activity)
            adapter = rvAdapter
            addItemDecoration(PostsListItemDecoration())
        }
        presenter.subscribeOnAllLikedPostsFromDB()
    }

    override fun showGetPostErrorDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle)
            .setTitle(getString(R.string.alert_dialog_error_title_text))
            .setMessage(getString(R.string.get_data_alert_dialog_message_text))
            .setCancelable(false)
            .setNegativeButton(
                R.string.get_data_alert_dialog_negative_button_text
            ) { _, _ -> (requireActivity()).finish() }
            .setPositiveButton(
                R.string.get_data_alert_dialog_positive_button_text
            ) { _, _ ->
                presenter.subscribeOnAllLikedPostsFromDB()
            }
            .create().show()
    }

    override fun showPostUpdateErrorToast() {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.post_update_error_text),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showPosts(posts: List<InfoRepresentationClass>) {
        likedPostsView.isVisible = true
        errorView.isGone = true
        zeroSavedPostsView.isGone = true
        rvAdapter.dataUnits = posts.toMutableList()
    }

    override fun showPostsView() {
        likedPostsView.isVisible = true
        errorView.isGone = true
        zeroSavedPostsView.isGone = true
    }

    override fun showErrorView() {
        likedPostsView.isGone = true
        errorView.isVisible = true
        zeroSavedPostsView.isGone = true
    }

    override fun showEmptyLikedPostsView() {
        likedPostsView.isGone = true
        errorView.isGone = true
        zeroSavedPostsView.isVisible = true
    }

    override fun onPostDismiss(post: PostData) {
       // нет реализации здесь. Пост не может быть задисмищен
    }

    override fun onPostLiked(post: PostData) {
        likePostOnApi(post)
    }

    override fun onPostDisliked(post: PostData) {
        dislikePostOnApi(post)
    }

    override fun onPostClicked(post: PostData) {
        (activity as MainActivity).openPostDetails(post)

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

    private fun networkIsNotAvailableMessage(toastText:String){
        Toast.makeText(
            requireContext(),
            toastText,
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        fun newInstance(): LikedPostsFragment {
            return LikedPostsFragment()
        }
    }
}
