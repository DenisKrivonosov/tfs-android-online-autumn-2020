package ru.krivonosovdenis.fintechapp.presentation.allposts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_all_posts.*
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.interfaces.AllPostsActions
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.MvpFragment
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import ru.krivonosovdenis.fintechapp.rvcomponents.ItemTouchHelperAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.ItemTouchHelperCallback
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsFeedAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsListItemDecoration

class AllPostsFragment : MvpFragment<AllPostsView, AllPostsPresenter>(), AllPostsView,
    AllPostsActions, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var rvAdapter: PostsFeedAdapter
    private val compositeDisposable = CompositeDisposable()

    companion object {
        fun newInstance(): AllPostsFragment {
            return AllPostsFragment()
        }
    }

    override fun getPresenter(): AllPostsPresenter = GlobalDI.INSTANCE.allPostsPresenter

    override fun getMvpView(): AllPostsView = this

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_posts, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allPostsSwipeRefreshLayout.setOnRefreshListener(this)
        (activity as MainActivity).showBottomNavigationTabs()
        rvAdapter = PostsFeedAdapter(this)
        with(allPostsView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
            addItemDecoration(PostsListItemDecoration())
        }
        val callback = ItemTouchHelperCallback(rvAdapter as ItemTouchHelperAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(allPostsView)
        if (GlobalDI.INSTANCE.isFirstAllPostsFragmentOpen) {
            getPresenter().loadPostsFromApiAndInsertIntoDB()
        }
        getPresenter().subscribeOnAllPostsFromDB()
    }

    override fun onRefresh() {
        getPresenter().loadPostsFromApiAndInsertIntoDB()
    }

    override fun setRefreshing(isRefreshing: Boolean) {
        allPostsSwipeRefreshLayout.isRefreshing = isRefreshing
    }

    override fun scrollViewToTop() {
        //Похоже эта штука не работает из-за того, что добавление постов отрабатывает позже
        //То есть оно скролится, но на первый пост из предыдущей коллекции постов
        //буду разбираться.
        allPostsView.scrollToPosition(0)
    }

    override fun showPosts(posts: List<PostFullData>) {
        allPostsView.isVisible = true
        loadingView.isGone = true
        errorView.isGone = true
        rvAdapter.posts = posts.toMutableList()
    }

    override fun showLoadingView() {
        allPostsView.isGone = true
        loadingView.isVisible = true
        errorView.isGone = true
    }

    override fun showPostsView() {
        allPostsView.isVisible = true
        loadingView.isGone = true
        errorView.isGone = true
    }

    override fun showErrorView() {
        allPostsView.isGone = true
        loadingView.isGone = true
        errorView.isVisible = true
    }

    override fun onPostDismiss(post: PostFullData) {
        getPresenter().deletePostApiAndDb(post)
    }

    override fun onPostLiked(post: PostFullData) {
        getPresenter().likePostOnApiAndDb(post)
    }

    override fun onPostClicked(post: PostFullData) {
        (activity as MainActivity).openPostDetails(post)
    }

}
