package ru.krivonosovdenis.fintechapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_all_posts.*
import ru.krivonosovdenis.fintechapp.MainActivity
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dbclasses.ApplicationDatabase
import ru.krivonosovdenis.fintechapp.rvcomponents.ItemTouchHelperAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.ItemTouchHelperCallback
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsFeedAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsListItemDecoration

class PostsFeedFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var rvAdapter: PostsFeedAdapter
    private val compositeDisposable = CompositeDisposable()

    companion object {
        fun newInstance(): PostsFeedFragment {
            return PostsFeedFragment()
        }
    }

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
        (activity as MainActivity).postsBottomNavigation.isVisible = true
        rvAdapter = PostsFeedAdapter(activity as MainActivity)
        allPostsRecyclerView.layoutManager = LinearLayoutManager(activity)
        allPostsRecyclerView.adapter = rvAdapter
        allPostsRecyclerView.addItemDecoration(PostsListItemDecoration())
        val callback = ItemTouchHelperCallback(rvAdapter as ItemTouchHelperAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(allPostsRecyclerView)
        showLoadingView()
        subscribeOnDb()
    }

    override fun onRefresh() {
        getFeedPostsFromApi()
    }

    private fun subscribeOnDb() {
        compositeDisposable.add(
            ApplicationDatabase.getInstance(context!!)?.feedPostsDao()?.subscribeOnAllFeedPosts()!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        if (it.count() > 0) {
                            showPostsView()
                            rvAdapter.posts = it.toMutableList()
                        }
                    },
                    onError = {
                        showErrorView()
                    }
                )
        )
    }

    private fun getFeedPostsFromApi() {
        compositeDisposable.add((activity as MainActivity).getPostsData()
            .doFinally {
                allPostsSwipeRefreshLayout.isRefreshing = false
                showPostsView()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        )
    }

    private fun showPostsView() {
        allPostsRecyclerView.isVisible = true
        loadingView.isGone = true
        errorView.isGone = true
    }

    private fun showLoadingView() {
        allPostsRecyclerView.isGone = true
        loadingView.isVisible = true
        errorView.isGone = true
    }

    private fun showErrorView() {
        allPostsRecyclerView.isGone = true
        loadingView.isGone = true
        errorView.isVisible = true
    }
}
