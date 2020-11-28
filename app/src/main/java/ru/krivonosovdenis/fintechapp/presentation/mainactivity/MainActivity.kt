package ru.krivonosovdenis.fintechapp.presentation.mainactivity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlinx.android.synthetic.main.activity_main.*
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.data.network.VkApiClient
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.presentation.allposts.AllPostsFragment
import ru.krivonosovdenis.fintechapp.presentation.appsettings.AppSettingsFragment
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.MvpActivity
import ru.krivonosovdenis.fintechapp.presentation.initloading.InitLoadingFragment
import ru.krivonosovdenis.fintechapp.presentation.likedposts.LikedPostsFragment
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsFragment
import ru.krivonosovdenis.fintechapp.presentation.sendpost.SendPostFragment
import ru.krivonosovdenis.fintechapp.presentation.userprofile.UserProfileFragment
import java.util.*


class MainActivity : MvpActivity<MainActivityView, MainActivityPresenter>(), MainActivityView,
    BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val ALL_POSTS_LIST = "all_posts_list"
        const val LIKED_POSTS_LIST = "liked_posts_list"
        const val POST_DETAILS = "post_details"
        const val INIT_LOADING = "init_loading"
    }

    override fun getPresenter(): MainActivityPresenter = GlobalDI.INSTANCE.mainActivityPresenter

    override fun getMvpView(): MainActivityView = this

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO добавить проверку на то, что у меня проставлен флаг, отвечающий за постоянный
        //ночной или постоянный дневной режим
//        AppCompatDelegate.setDefaultNightMode(
//            AppCompatDelegate.MODE_NIGHT_AUTO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        postsBottomNavigation.setOnNavigationItemSelectedListener(this)
        postsBottomNavigation.menu.findItem(R.id.actionAllPosts).isChecked = true
        getPresenter().subscribeBottomTabsOnDb()
        if (!VK.isLoggedIn()) {
            showInitLoadingFragment()
            openVkLogin()
        } else {
            showAllPostsFragment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val nightMode = AppCompatDelegate.getDefaultNightMode()
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            menu.findItem(R.id.changeNightMode).apply {
                setIcon(R.drawable.toolbar_sun_icon)
                setTitle(R.string.toolbar_menu_set_light_theme)
            }
        } else {
            menu.findItem(R.id.changeNightMode).apply {
                setIcon(R.drawable.toolbar_moon_icon)
                setTitle(R.string.toolbar_menu_set_dark_theme)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.changeNightMode -> {
                val nightMode = AppCompatDelegate.getDefaultNightMode()
                if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                recreate()
            }
            R.id.settings ->{
                //TODO add action to settings
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                GlobalDI.INSTANCE.sessionManager.storeSessionToken(token.accessToken)
                Log.e("mainActivity", "token:${token.accessToken}")
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


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.isChecked) {
            return true
        }
        for (i in 0 until postsBottomNavigation.menu.size()) {
            val menuItem: MenuItem = postsBottomNavigation.menu.getItem(i)
            menuItem.isChecked = menuItem.itemId == item.itemId
        }
        when (item.itemId) {
            R.id.actionAllPosts -> {
                showAllPostsFragment()
            }
            R.id.actionLikedPosts -> {
                showLikedPostsFragment()
            }
            R.id.actionUserProfile -> {
                showUserProfileFragment()
            }
        }
        return true
    }

    private fun showLikedPostsFragment() {
        postsBottomNavigation.isVisible = true
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, LikedPostsFragment.newInstance(), LIKED_POSTS_LIST)
            .commit()
    }

    private fun showAllPostsFragment() {
        postsBottomNavigation.isVisible = true
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, AllPostsFragment.newInstance(), ALL_POSTS_LIST)
            .commit()
    }

    private fun showUserProfileFragment() {
        postsBottomNavigation.isVisible = true
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, UserProfileFragment.newInstance())
            .commit()
    }

    private fun showSendPostFragment() {
        postsBottomNavigation.isVisible = true
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, SendPostFragment.newInstance())
            .commit()
    }

    private fun showAppSettingsFragment() {
        postsBottomNavigation.isVisible = true
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, AppSettingsFragment.newInstance())
            .commit()
    }

    private fun showInitLoadingFragment() {
        postsBottomNavigation.isVisible = false
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, InitLoadingFragment.newInstance(), INIT_LOADING)
            .commit()
    }

    fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
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

    override fun openPostDetails(post: PostFullData) {
        val postDetailsFragment =
            PostDetailsFragment.newInstance(post.postId, post.sourceId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, postDetailsFragment, POST_DETAILS)
            .addToBackStack(null)
            .commit()
    }

    override fun showBottomNavigationTabs() {
        postsBottomNavigation.isVisible = true
    }

    override fun setLikedPostsVisibility(visibilityFlag: Boolean) {
        postsBottomNavigation.menu.findItem(R.id.actionLikedPosts).isVisible = visibilityFlag
    }

}
