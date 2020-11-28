package ru.krivonosovdenis.fintechapp.presentation.likedposts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_liked_posts.*
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.interfaces.LikedPostsActions
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.MvpFragment
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import ru.krivonosovdenis.fintechapp.rvcomponents.LikedPostsAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsListItemDecoration

class LikedPostsFragment : MvpFragment<LikedPostsView, LikedPostsPresenter>(), LikedPostsView,
    LikedPostsActions {

    private lateinit var rvAdapter: LikedPostsAdapter

    companion object {
        fun newInstance(): LikedPostsFragment {
            return LikedPostsFragment()
        }
    }

    override fun getPresenter(): LikedPostsPresenter = GlobalDI.INSTANCE.allLikedPostsPresenter

    override fun getMvpView(): LikedPostsView = this

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
        rvAdapter = LikedPostsAdapter(this)

        with(likedPostsView) {
            layoutManager = LinearLayoutManager(activity)
            adapter = rvAdapter
            addItemDecoration(PostsListItemDecoration())
        }
        getPresenter().subscribeOnAllLikedPostsFromDB()
    }

    override fun showGetPostErrorDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.alert_dialog_error_title_text))
            .setMessage(getString(R.string.get_data_alert_dialog_message_text))
            .setCancelable(false)
            .setNegativeButton(
                R.string.get_data_alert_dialog_negative_button_text
            ) { _, _ -> (requireActivity()).finish() }
            .setPositiveButton(
                R.string.get_data_alert_dialog_positive_button_text
            ) { _, _ ->
                getPresenter().subscribeOnAllLikedPostsFromDB()
            }
            .create().show()
    }

    override fun showPosts(posts: List<PostFullData>) {
        likedPostsView.isVisible = true
        errorView.isGone = true
        rvAdapter.posts = posts.toMutableList()
    }

    override fun showPostsView() {
        likedPostsView.isVisible = true
        errorView.isGone = true
    }

    override fun showErrorView() {
        likedPostsView.isGone = true
        errorView.isVisible = true
    }

    override fun onPostClicked(post: PostFullData) {
        (activity as MainActivity).openPostDetails(post)
    }
}
