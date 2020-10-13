package ru.krivonosovdenis.fintechapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import ru.krivonosovdenis.fintechapp.dataclasses.PostRenderData
import ru.krivonosovdenis.fintechapp.fragments.PostDetailsFragment
import ru.krivonosovdenis.fintechapp.fragments.PostsFeedFragment
import ru.krivonosovdenis.fintechapp.fragments.PostsLikedFragment
import ru.krivonosovdenis.fintechapp.interfaces.AllPostsActions
import ru.krivonosovdenis.fintechapp.parserfunctions.parseGroupsResponse
import ru.krivonosovdenis.fintechapp.parserfunctions.parsePostsResponse
import java.io.IOException

class MainActivity : AppCompatActivity(), AllPostsActions,
    BottomNavigationView.OnNavigationItemSelectedListener {
    //В связи с добавлением фрагментов бизнес логика и UI поменялись. Отображение UI мы перенесли на
    //фрагменты. Бизнес же логику я пока оставил здесь. В активитити будут храниться данные и
    //методы получения этих данных, доступные для приатаченных к этой активити фрагментов
    //впоследствие мы перенесем логику работы с данными и их хранене в отдельные классы, например
    //viewmodel. Та как особо с viewmodel и реактивщиной пока не работал, рещил не бежать впереди паровоза
    //и оставить бизнеслогику здесь. Поэтому я добавил несколько публичных переменных, отвечающих за данные

    companion object {
        const val POST_RENDER_DATA = "posts_render_data"
        const val LAST_UPDATE_DATE = "last_update_date"
    }

    //Захардкодил определенную дату для теста. Имитация случая, когда последний раз посты забирали давно
    var lastUpdateDate = 1500000L
    private var renderPostsData = ArrayList<PostRenderData>()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(POST_RENDER_DATA, renderPostsData)
        outState.putLong(LAST_UPDATE_DATE, lastUpdateDate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        postsBottomNavigation.setOnNavigationItemSelectedListener(this)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, PostsFeedFragment.newInstance(), "allPostsList")
                .commit()
            postsBottomNavigation.menu.findItem(R.id.actionLikedPosts).isVisible = false
        } else {
            lastUpdateDate = savedInstanceState.getLong(LAST_UPDATE_DATE)
            renderPostsData =
                savedInstanceState.getParcelableArrayList<PostRenderData>(POST_RENDER_DATA) as ArrayList<PostRenderData>
            postsBottomNavigation.menu.findItem(R.id.actionLikedPosts).isVisible =
                renderPostsData.filter { it.isLiked }.count() != 0
        }
        postsBottomNavigation.menu.findItem(R.id.actionAllPosts).isChecked = true

    }


    override fun onPostDismiss(postId: String) {
        val innerPosts = renderPostsData.toMutableList()
        val position = innerPosts.indexOfFirst { it.postId == postId }
        innerPosts.removeAt(position)
        renderPostsData = innerPosts as ArrayList<PostRenderData>
        if (renderPostsData.filter { it.isLiked }.count() == 0) {
            postsBottomNavigation.menu.findItem(R.id.actionLikedPosts).isVisible = false
        }
    }

    override fun onPostLiked(postId: String) {
        val position = renderPostsData.indexOfFirst { it.postId == postId }
        renderPostsData[position].likesCount += 1
        postsBottomNavigation.menu.findItem(R.id.actionLikedPosts).isVisible = true
    }

    override fun onPostClicked(postId: String) {
        val position = renderPostsData.indexOfFirst { it.postId == postId }
        val postDetailsFragment =
            PostDetailsFragment.newInstance(renderPostsData[position])
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, postDetailsFragment, "postDetails")
            .addToBackStack(null)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //Если вкладка и так открыта - ничего не делаем
        if (item.isChecked) {
            return true
        }
        for (i in 0 until postsBottomNavigation.menu.size()) {
            val menuItem: MenuItem = postsBottomNavigation.menu.getItem(i)
            val isChecked = menuItem.itemId == item.itemId
            menuItem.isChecked = isChecked
        }

        when (item.itemId) {
            R.id.actionAllPosts -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        PostsFeedFragment.newInstance(),
                        "allPostsList"
                    )
                    .commit()
            }
            R.id.actionLikedPosts -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        PostsLikedFragment.newInstance(),
                        "likedPostsList"
                    )
                    .commit()
            }
        }
        return true
    }

    //пока для теста сделал так:
    //При обычном запросе забираем посты с датой меньшей текущей
    //Если с рефреша - то забираем все посты
    // В нашу мок дату специально добавим несколько постов с датой из будущего, чтоб
    //отобразить их только при рефреше
    fun getPostsData(
        lastUpdateDate: Long,
        fromRefresh: Boolean = false
    ): ArrayList<PostRenderData> {
        val postsRawData = readDataFromFile("Posts.json")
        val postsData = parsePostsResponse(postsRawData)
        val groupsRawData = readDataFromFile("Groups.json")
        val groupsData = parseGroupsResponse(groupsRawData)
        val currentRenderData = renderPostsData.toMutableList()
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
            currentRenderData.add(
                PostRenderData(
                    "${postData.sourceId}_${postData.postId}",
                    group.avatar,
                    group.name,
                    DateTime(postData.date),
                    postData.text,
                    postData.photo,
                    postData.likesCount,
                    isLiked = false,
                    isCommented = false,
                    isShared = false,
                    isHidden = false
                )
            )
        }
        currentRenderData.sortByDescending { it.date }
        //Помимо возвращения итоговых данных, мы также обновляем список,
        // хранящийся в активности, до актуального
        renderPostsData = currentRenderData as ArrayList<PostRenderData>
        return currentRenderData
    }

    fun getLikedPostsData(): ArrayList<PostRenderData> {
        return renderPostsData.filter { it.isLiked } as ArrayList<PostRenderData>
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
