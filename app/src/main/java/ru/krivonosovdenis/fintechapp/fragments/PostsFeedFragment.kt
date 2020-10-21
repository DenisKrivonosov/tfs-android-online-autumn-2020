package ru.krivonosovdenis.fintechapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
import java.util.concurrent.TimeUnit

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
        return inflater.inflate(
            R.layout.fragment_all_posts, container,
            false
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allPostsSwipeRefreshLayout.setOnRefreshListener(this)
        (activity as MainActivity).postsBottomNavigation.visibility = VISIBLE
        rvAdapter = PostsFeedAdapter(activity as MainActivity)
        allPostsRecyclerView.layoutManager = LinearLayoutManager(activity)
        allPostsRecyclerView.adapter = rvAdapter
        allPostsRecyclerView.addItemDecoration(PostsListItemDecoration())
        val callback = ItemTouchHelperCallback(rvAdapter as ItemTouchHelperAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(allPostsRecyclerView)
    }

    //Логику получения данных во фрагменте я реализовал в этом колбеке. Обрабатывается случай
    // поворота экрана. При реализации в этом колбеке я могу быть уверен, что у активности
    // вызвался метод onCreate и что мы обработали в этом методе bundle, в котором хранятся
    //время последнего апдейта и данные. По этой же причине в главной активности savedInstanceState
    // bundle я обрабатываю в onCreate, а не в  onRestoreInstanceState. onRestoreInstanceState
    //вызывается в произвольное время и, видимо, позже чем onCreate
    // Потом, опять же, можно будет переписать на другую логику, когда изменится логика работы с данными
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if ((activity as MainActivity).renderPostsData.count() == 0) {
            showLoadingView()
            getPosts(1601845296000L, true)
        } else {
            getPosts(1601845296000L)
        }
    }

    override fun onRefresh() {
        getPosts(1801845296000L, true)
    }


    //Пока я передаю дату сюда, как логику для демонстрации работы. Потом даты здесь не будет.
    // Будет только храниться последняя дата обновления данных в источнике данных и все
    private fun getPosts(newLastUpdate: Long, fromNetwork: Boolean = false) {
        compositeDisposable.add((activity as MainActivity).getPostsData(newLastUpdate, fromNetwork)
            //Добавляем искуственную задержку при имитации поиска из сети (по факту при чтении из файла)
            .compose {
                if (fromNetwork) it.delay(3000, TimeUnit.MILLISECONDS) else {
                    it
                }
            }
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
                    if (fromNetwork) {
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
                getPosts(1801845296000L)
            }
            .create().show()
    }

    private fun showPostsView() {
        allPostsRecyclerView.visibility = VISIBLE
        loadingView.visibility = GONE
        errorView.visibility = GONE
    }

    private fun showLoadingView() {
        allPostsRecyclerView.visibility = GONE
        loadingView.visibility = VISIBLE
        errorView.visibility = GONE
    }

    private fun showErrorView() {
        allPostsRecyclerView.visibility = GONE
        loadingView.visibility = GONE
        errorView.visibility = VISIBLE
    }
}
