package ru.krivonosovdenis.fintechapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_all_posts.*
import org.joda.time.DateTime
import ru.krivonosovdenis.fintechapp.dataclasses.PostRenderData
import ru.krivonosovdenis.fintechapp.dataclasses.groupsdataclasses.GroupsApiResponse
import ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses.NewsfeedApiResponse
import ru.krivonosovdenis.fintechapp.fragments.InitLoadingFragment
import ru.krivonosovdenis.fintechapp.fragments.PostDetailsFragment
import ru.krivonosovdenis.fintechapp.fragments.PostsFeedFragment
import ru.krivonosovdenis.fintechapp.fragments.PostsLikedFragment
import ru.krivonosovdenis.fintechapp.interfaces.AllPostsActions
import ru.krivonosovdenis.fintechapp.networkutils.VkApiClient


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
        const val ALL_POSTS_LIST = "all_posts_list"
        const val LIKED_POSTS_LIST = "all_posts_list"
        const val POST_DETAILS = "post_details"
        const val INIT_LOADING = "init_loading"
        const val ADD_POST_TYPE = "post"
        const val DELETE_POST_TYPE = "wall"
    }

    var renderPostsData = ArrayList<PostRenderData>()
    private val compositeDisposable = CompositeDisposable()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(POST_RENDER_DATA, renderPostsData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        postsBottomNavigation.setOnNavigationItemSelectedListener(this)
        postsBottomNavigation.menu.findItem(R.id.actionAllPosts).isChecked = true
        postsBottomNavigation.menu.findItem(R.id.actionLikedPosts).isVisible =
            renderPostsData.any { it.isLiked }

        if (!VK.isLoggedIn()) {
            showInitLoadingFragment()
            openVkLogin()
        } else if (savedInstanceState == null) {
            showAllPostsFragment()
        } else {
            renderPostsData =
                savedInstanceState.getParcelableArrayList<PostRenderData>(POST_RENDER_DATA) as ArrayList<PostRenderData>
            postsBottomNavigation.menu.findItem(R.id.actionLikedPosts).isVisible =
                renderPostsData.any { it.isLiked }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                //Сейчас у меня в приложении такая бага. Если при открытии окна логина вкашным sdk
                //выйти из окна webview браузера, а потом попытаться залогиниться заново, то токен
                //отдастся, но он будет невалидным
                SessionManager(this@MainActivity).storeSessionToken(token.accessToken)
                VkApiClient.accessToken = token.accessToken
                showAllPostsFragment()
            }

            override fun onLoginFailed(errorCode: Int) {
                showVkLoginErrorAlert()
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
            showVkLoginErrorAlert()
        }
    }

    override fun onPostDismiss(post: PostRenderData) {
        val innerPosts = renderPostsData.toMutableList()
        val position = innerPosts.indexOfFirst { it.postId == post.postId }
        innerPosts.removeAt(position)
        renderPostsData = innerPosts as ArrayList<PostRenderData>
        sendDeletePostToServer(post)
        postsBottomNavigation.menu.findItem(R.id.actionLikedPosts).isVisible =
            renderPostsData.any { it.isLiked }
    }

    override fun onPostLiked(post: PostRenderData) {
        val position = renderPostsData.indexOfFirst { it.postId == post.postId }
        renderPostsData[position].likesCount += 1
        postsBottomNavigation.menu.findItem(R.id.actionLikedPosts).isVisible = true
        sendLikeToServer(post)
    }

    override fun onPostClicked(post: PostRenderData) {
        val position = renderPostsData.indexOfFirst { it.postId == post.postId }
        val postDetailsFragment =
            PostDetailsFragment.newInstance(renderPostsData[position])
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, postDetailsFragment, POST_DETAILS)
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
            menuItem.isChecked = menuItem.itemId == item.itemId
        }
        when (item.itemId) {
            R.id.actionAllPosts -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        PostsFeedFragment.newInstance(),
                        ALL_POSTS_LIST
                    )
                    .commit()
            }
            R.id.actionLikedPosts -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        PostsLikedFragment.newInstance(),
                        LIKED_POSTS_LIST
                    )
                    .commit()
            }
        }
        return true
    }

    //Здесь ситуация такая. Сейчас endpoint для получения постов следующий:
    //https://api.vk.com/method/newsfeed.get Он отдает данные в следующем формате
    // -response
    //    -items
    //    -profiles
    //    -groups
    //Где items - сами посты, а profiles и groups - доп инфа о профилях и группах,
    // оставиших эти посты. Получается, что для рендера инфы о посте вся инфа приходит с одного
    //эндпоинта. Но в учебных целях я допустил, что инфы об постерах нет и забираю с эндпоинта
    // /https://api.vk.com/method/groups.getById Цель - понять как комбинировать два
    // последовательных запроса в рамках rx. Поэтому сейчас отображаются посты только от групп.
    // В конечной версии приложения можно будет перенести всё на один запрос и отобразить посты
    //от пользователей тоже
    fun getPostsData(forceApiLoading: Boolean = false): Observable<ArrayList<PostRenderData>> {
        if (renderPostsData.count() > 0 && !forceApiLoading) {
            return Observable.just(renderPostsData)
        }
        return VkApiClient.getAuthRetrofitClient().getNewsFeed()
            .flatMap { addGroupsInfoToPosts(it) }.toObservable()
    }

    private fun addGroupsInfoToPosts(newsfeedApiResponse: NewsfeedApiResponse): Single<ArrayList<PostRenderData>> {
        val groups = getDistinctGroups(newsfeedApiResponse)
        return VkApiClient.getAuthRetrofitClient().getGroups(groups).flatMap {
            Single.just(combinePostsAndGroups(newsfeedApiResponse, it))
        }
    }

    private fun combinePostsAndGroups(
        postsData: NewsfeedApiResponse,
        groupsData: GroupsApiResponse
    ): ArrayList<PostRenderData> {
        val currentRenderData = ArrayList<PostRenderData>()
        postsData.response.items.forEach iterator@{ postData ->
            //Пока я отображаю только посты от групп. Когда вернусь к сбору всей информации из одного
            //эндпоинта - буду отображать также посты от пользователей
            if (postData.sourceId > 0) {
                return@iterator
            }
            val group = groupsData.response.first { it.id == -postData.sourceId }
            try {
                currentRenderData.add(
                    PostRenderData(
                        postData.postId,
                        postData.sourceId,
                        group.photo200,
                        group.name,
                        DateTime(postData.date * 1000L),
                        postData.text ?: "",
                        postData.attachments?.get(0)?.photo?.sizes?.last()?.url ?: "",
                        postData.likes?.count ?: 0,
                        postData.likes?.userLikes == 1,
                        isCommented = false,
                        isShared = false,
                        isHidden = false
                    )
                )
            } catch (e: Exception) {
                //add exception handler here
            }

        }

        currentRenderData.sortByDescending { it.date }
        renderPostsData = currentRenderData

        // не знаю можно ли так делать По факту при вызове данных нам надо перехватить rx поток и показать
        //вьюху не во фрагменте, а в активности. Возможно, стоит перенести вызов этой проверки
        //во фрагмент или реализовать как-то по-другому
        runOnUiThread {
            postsBottomNavigation.menu.findItem(R.id.actionLikedPosts).isVisible =
                renderPostsData.any { it.isLiked }
        }

        return currentRenderData
    }

    private fun sendLikeToServer(post: PostRenderData) {
        compositeDisposable.add(VkApiClient.getAuthRetrofitClient()
            .addLike(ADD_POST_TYPE, post.sourceId, post.postId)
            .doFinally {
                allPostsSwipeRefreshLayout.isRefreshing = false
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { it ->
                    renderPostsData.first { it.postId == post.postId && it.sourceId == post.sourceId }.likesCount =
                        it.response.likes
                },
                onError = {
                },
            )
        )
    }

    private fun sendDeletePostToServer(post: PostRenderData) {
        compositeDisposable.add(VkApiClient.getAuthRetrofitClient()
            .deletePostFromFeed(DELETE_POST_TYPE, post.sourceId, post.postId)
            .doFinally {
                allPostsSwipeRefreshLayout.isRefreshing = false
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { _ ->

                },
                onError = {
                },
            )
        )
    }

    private fun getDistinctGroups(apiResponse: NewsfeedApiResponse): String {
        return apiResponse.response.items.map { it.sourceId }.filter { it < 0 }.map { -it }
            .joinToString(separator = ",")
    }

    fun getLikedPostsData(): ArrayList<PostRenderData> {
        return renderPostsData.filter { it.isLiked } as ArrayList<PostRenderData>
    }

    private fun showAllPostsFragment() {
        postsBottomNavigation.isVisible = true
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, PostsFeedFragment.newInstance(), ALL_POSTS_LIST)
            .commit()
    }

    private fun showInitLoadingFragment() {
        postsBottomNavigation.isVisible = false
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, InitLoadingFragment.newInstance(), INIT_LOADING)
            .commit()
    }

    private fun openVkLogin() {
        VK.login(this, arrayListOf(VKScope.WALL, VKScope.FRIENDS))
    }

    fun showVkLoginErrorAlert() {
        AlertDialog.Builder(this@MainActivity)
            .setTitle(getString(R.string.vk_login_alert_dialog_title_text))
            .setMessage(getString(R.string.vk_login_alert_dialog_message_text))
            .setCancelable(false)
            .setNegativeButton(
                R.string.vk_login_alert_dialog_negative_button_text
            ) { _, _ -> (this@MainActivity).finish() }
            .setPositiveButton(
                R.string.vk_login_alert_dialog_positive_button_text
            ) { _, _ ->
                openVkLogin()
            }
            .create().show()
    }
}
