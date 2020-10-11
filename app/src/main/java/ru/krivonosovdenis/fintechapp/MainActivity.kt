package ru.krivonosovdenis.fintechapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import ru.krivonosovdenis.fintechapp.dataclasses.PostRenderData
import ru.krivonosovdenis.fintechapp.parserfunctions.parseGroupsResponse
import ru.krivonosovdenis.fintechapp.parserfunctions.parsePostsResponse
import ru.krivonosovdenis.fintechapp.rvcomponents.ItemTouchHelperAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.ItemTouchHelperCallback
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsFeedAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsFeedItemDecoration
import java.io.IOException

class MainActivity : AppCompatActivity(), OnRefreshListener {
    //Захардкодил определенную дату для теста. Имитация случая, когда последний раз посты забирали давно
    private var lastUpdateDate = 1500000L
    private lateinit var rvAdapter: PostsFeedAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        swipeRefreshLayout.setOnRefreshListener(this)
        rvAdapter = PostsFeedAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = rvAdapter
        recyclerView.addItemDecoration(PostsFeedItemDecoration())
        val callback = ItemTouchHelperCallback(rvAdapter as ItemTouchHelperAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        //Имитируем поход в сеть
        rvAdapter.posts = getPostsData(lastUpdateDate)
        //При первом запросе увеличим дату последнего апдейта, но зададим ее такой, чтоб файле были
        //посты с датой большей текущей
        lastUpdateDate = 1601845296000L
    }

    override fun onRefresh() {
        rvAdapter.posts = getPostsData(lastUpdateDate, true)
        swipeRefreshLayout.isRefreshing = false
        //Дата в далеком будушем
        lastUpdateDate = 1801845296000L
        recyclerView.post(Runnable {
            recyclerView.smoothScrollToPosition(0)

        })
    }

    //пока для теста сделал так:
    //При обычном запросе забираем посты с датой меньшей текущей
    //Если с рефреша - то забираем все посты
    // В нашу мок дату специально добавим несколько постов с датой из будущего, чтоб
    //отобразить их только при рефреше
    private fun getPostsData(
        lastUpdateDate: Long,
        fromRefresh: Boolean = false
    ): ArrayList<PostRenderData> {
        val postsRawData = readDataFromFile("Posts.json")
        val postsData = parsePostsResponse(postsRawData)
        val groupsRawData = readDataFromFile("Groups.json")
        val groupsData = parseGroupsResponse(groupsRawData)

        val renderData = rvAdapter.posts.toMutableList()
        postsData.forEach iterator@{ postData ->
            //не показываем последний пост, если запрос не с рефреша
            //Здесь пока оставил такой же return, который тебе не понравился
            //Пока хз как правильно брейкать текущую итерацию foreach, надо поресерчить
            if (!fromRefresh && postData.date > 1601845296000) {
                return@iterator
            }
            if (postData.date < lastUpdateDate) {
                return@iterator
            }
            val group = groupsData.first { it.id == postData.sourceId }
            renderData.add(
                PostRenderData(
                    "${postData.sourceId}_${postData.postId}",
                    group.avatar,
                    group.name,
                    DateTime(postData.date),
                    postData.text,
                    postData.photo,
                    isLiked = false,
                    isCommented = false,
                    isShared = false,
                    isHidden = false
                )
            )
        }
        renderData.sortByDescending { it.date }
        return renderData as ArrayList<PostRenderData>
    }

    private fun readDataFromFile(fileName: String): String {
        return try {
            val input = assets.open(fileName)
            val size: Int = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            String(buffer)
        } catch (e: IOException) {
            ""
        }
    }
}
