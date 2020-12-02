package ru.krivonosovdenis.fintechapp.presentation.postsfeed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import ru.krivonosovdenis.fintechapp.interfaces.CommonAdapterActions
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.MvpFragment
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import ru.krivonosovdenis.fintechapp.rvcomponents.*

class PostsFeedFragment : MvpFragment<PostsFeedView, PostsFeedPresenter>(), PostsFeedView,
    CommonAdapterActions, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var rvAdapter: CommonRVAdapter
    private val compositeDisposable = CompositeDisposable()


    override fun getPresenter(): PostsFeedPresenter = GlobalDI.INSTANCE.allPostsPresenter

    override fun getMvpView(): PostsFeedView = this

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
        rvAdapter = CommonRVAdapter(this)
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

    override fun showPosts(posts: List<PostFullData>) {
        val oldPosts = rvAdapter.dataUnits
        allPostsView.isVisible = true
        loadingView.isGone = true
        errorView.isGone = true
        rvAdapter.dataUnits = posts.toMutableList()
        //Смотрим появились ли новые посты. Если появились - скролим скроллвью наверх
        posts.forEach { newPost->
            if(oldPosts.find { ((it as PostFullData).sourceId == newPost.sourceId)
                        &&((it).postId == newPost.postId)} == null){
                allPostsView.postDelayed(Runnable {
                    allPostsView.smoothScrollToPosition(
                        0,
                    )
                }, 300)
            }
        }
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

    override fun showDbGetFeedErrorView() {
        allPostsView.isGone = true
        loadingView.isGone = true
        errorView.isVisible = true
    }

    override fun showPostUpdateErrorToast() {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.post_update_error_text),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPostDismiss(post: PostFullData) {
        Log.e("heredeletingpost", post.text)
        getPresenter().deletePostOnApiAndDb(post)
    }

    override fun onPostLiked(post: PostFullData) {
        getPresenter().likePostOnApiAndDb(post)
    }

    override fun onPostDisliked(post: PostFullData) {
        getPresenter().dislikePostOnApiAndDb(post)
    }

    override fun onPostClicked(post: PostFullData) {
        (activity as MainActivity).openPostDetails(post)
    }

    companion object {
        fun newInstance(): PostsFeedFragment {
            return PostsFeedFragment()
        }
    }

}
