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
import io.reactivex.Completable
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
import ru.krivonosovdenis.fintechapp.dbclasses.ApplicationDatabase
import ru.krivonosovdenis.fintechapp.fragments.InitLoadingFragment
import ru.krivonosovdenis.fintechapp.fragments.PostDetailsFragment
import ru.krivonosovdenis.fintechapp.fragments.PostsFeedFragment
import ru.krivonosovdenis.fintechapp.fragments.PostsLikedFragment
import ru.krivonosovdenis.fintechapp.interfaces.AllPostsActions
import ru.krivonosovdenis.fintechapp.networkutils.VkApiClient

class MainActivity : AppCompatActivity(), AllPostsActions,
    BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val ALL_POSTS_LIST = "all_posts_list"
        const val LIKED_POSTS_LIST = "all_posts_list"
        const val POST_DETAILS = "post_details"
        const val INIT_LOADING = "init_loading"
        const val ADD_POST_TYPE = "post"
        const val DELETE_POST_TYPE = "wall"
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        postsBottomNavigation.setOnNavigationItemSelectedListener(this)
        postsBottomNavigation.menu.findItem(R.id.actionAllPosts).isChecked = true

        //Удаляем все посты при открытии приложения
        compositeDisposable.add(
            ApplicationDatabase.getInstance(this)?.feedPostsDao()?.deleteAllPosts()!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
        //Таба с лайкнутыми постами также теперь подписана на DB
        compositeDisposable.add(
            ApplicationDatabase.getInstance(this)?.feedPostsDao()?.subscribeOnLikedCount()!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        postsBottomNavigation.menu.findItem(R.id.actionLikedPosts).isVisible =
                            it > 0
                    },
                )
        )

        if (!VK.isLoggedIn()) {
            showInitLoadingFragment()
            openVkLogin()
        } else {
            compositeDisposable.add(getPostsData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = {
                        showGetDataErrorDialog()
                    }
                )
            )
            showAllPostsFragment()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                SessionManager(this@MainActivity).storeSessionToken(token.accessToken)
                VkApiClient.accessToken = token.accessToken
                compositeDisposable.add(
                    getPostsData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                )
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
        sendDeletePostToServer(post)
        removePostFromDb(post)
    }

    override fun onPostLiked(post: PostRenderData) {
        sendLikeToServer(post)
        updateDBPostLiked(post)
    }

    private fun removePostFromDb(post: PostRenderData) {
        compositeDisposable.add(
            ApplicationDatabase.getInstance(this)?.feedPostsDao()
                ?.deletePostById(post.postId, post.sourceId)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    private fun updateDBPostLiked(post: PostRenderData) {
        compositeDisposable.add(
            ApplicationDatabase.getInstance(this)?.feedPostsDao()
                ?.setPostLikeById(post.postId, post.sourceId, post.likesCount + 1)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    override fun onPostClicked(post: PostRenderData) {
        val postDetailsFragment =
            PostDetailsFragment.newInstance(post.postId, post.sourceId)
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

    fun getPostsData(): Completable {
        return VkApiClient.getAuthRetrofitClient().getNewsFeed()
            .flatMap { addGroupsInfoToPosts(it) }
            .flatMapCompletable {
                ApplicationDatabase.getInstance(this)?.feedPostsDao()?.insertPostsInDb(it)
            }
    }

    private fun addGroupsInfoToPosts(newsfeedApiResponse: NewsfeedApiResponse): Single<ArrayList<PostRenderData>> {
        val groups = getDistinctGroups(newsfeedApiResponse)
        return VkApiClient.getAuthRetrofitClient().getGroups(groups)
            .flatMap {
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
                showGetDataErrorDialog()
            }

        }
        currentRenderData.sortByDescending { it.date }
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
            .subscribe()
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
            .subscribe()
        )
    }

    private fun getDistinctGroups(apiResponse: NewsfeedApiResponse): String {
        return apiResponse.response.items.map { it.sourceId }.filter { it < 0 }.map { -it }
            .joinToString(separator = ",")
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

    private fun showGetDataErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.alert_dialog_error_title_text))
            .setMessage(getString(R.string.get_data_alert_dialog_message_text))
            .setCancelable(false)
            .setNegativeButton(
                R.string.get_data_alert_dialog_negative_button_text
            ) { _, _ -> this.finish() }
            .setPositiveButton(
                R.string.get_data_alert_dialog_positive_button_text
            ) { _, _ ->
                getPostsData()
            }
            .create().show()
    }
}
