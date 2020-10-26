package ru.krivonosovdenis.fintechapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if ((activity as MainActivity).renderPostsData.count() == 0) {
            showLoadingView()
            getFeedPosts()
        } else {
            getFeedPosts()
        }
    }

    override fun onRefresh() {
        getFeedPosts(true)
    }

    private fun getFeedPosts(forceApiLoading: Boolean = false) {
        compositeDisposable.add((activity as MainActivity).getPostsData(forceApiLoading)
            .doFinally {
                allPostsSwipeRefreshLayout.isRefreshing = false
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    rvAdapter.posts = it
                },
                onError = {
                    if (forceApiLoading) {
                        showErrorView()
                    } else {
                        showGetDataErrorDialog()
                    }
                },
                onComplete = {
                    showPostsView()
                }
            )
        )
    }

    private fun showGetDataErrorDialog() {
        AlertDialog.Builder(activity as MainActivity)
            .setTitle(getString(R.string.get_data_alert_dialog_title_text))
            .setMessage(getString(R.string.get_data_alert_dialog_message_text))
            .setCancelable(false)
            .setNegativeButton(
                R.string.get_data_alert_dialog_negative_button_text
            ) { _, _ -> (activity as MainActivity).finish() }
            .setPositiveButton(
                R.string.get_data_alert_dialog_positive_button_text
            ) { _, _ ->
                getFeedPosts()
            }
            .create().show()
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
