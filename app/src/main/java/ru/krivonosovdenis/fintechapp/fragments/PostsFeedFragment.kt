package ru.krivonosovdenis.fintechapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
        val lastUpdate = (context as MainActivity).lastUpdateDate
        rvAdapter.posts = (activity as MainActivity).getPostsData(lastUpdate)
        (activity as MainActivity).lastUpdateDate = 1601845296000L
    }

    override fun onRefresh() {
        val lastUpdate = (context as MainActivity).lastUpdateDate
        val newPostsData = (activity as MainActivity).getPostsData(lastUpdate, true)
        rvAdapter.posts = newPostsData
        allPostsSwipeRefreshLayout.isRefreshing = false
        //Дата в далеком будушем
        (activity as MainActivity).lastUpdateDate = 1801845296000L
        allPostsRecyclerView.post {
            allPostsRecyclerView.smoothScrollToPosition(0)
        }
    }
}
