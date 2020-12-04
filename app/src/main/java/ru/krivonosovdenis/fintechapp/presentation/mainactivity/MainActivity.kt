package ru.krivonosovdenis.fintechapp.presentation.mainactivity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.presenter.ProvidePresenter
import ru.krivonosovdenis.fintechapp.ApplicationClass
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.SessionManager
import ru.krivonosovdenis.fintechapp.data.network.interceptors.VkTokenInterceptor
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.presentation.appsettings.AppSettingsFragment
import ru.krivonosovdenis.fintechapp.presentation.initloading.InitLoadingFragment
import ru.krivonosovdenis.fintechapp.presentation.likedposts.LikedPostsFragment
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsFragment
import ru.krivonosovdenis.fintechapp.presentation.postsfeed.PostsFeedFragment
import ru.krivonosovdenis.fintechapp.presentation.sendpost.SendPostFragment
import ru.krivonosovdenis.fintechapp.presentation.userprofile.UserProfileFragment
import java.util.*
import javax.inject.Inject

class MainActivity : MvpAppCompatActivity(), MainActivityView,
    BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
//    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    @ProvidePresenter
    fun provide() = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme()
        (applicationContext as ApplicationClass).addMainActivityComponent()
        (applicationContext as ApplicationClass).mainActivityComponent?.inject(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(appGlobalToolbar)
        postsBottomNavigation.setOnNavigationItemSelectedListener(this)
        postsBottomNavigation.menu.findItem(R.id.actionAllPosts).isChecked = true
        presenter.subscribeBottomTabsOnDb()
        if (!VK.isLoggedIn()) {
            if(!(application as ApplicationClass).isNetworkAvailableVariable){
                showNoNetworkNoVkTokenAlert()
            }
            else{
                showInitLoadingFragment()
                openVkLogin()
            }

        } else {
            VkTokenInterceptor.vkToken = sessionManager.getToken()
            if (savedInstanceState == null) {
                showAllPostsFragment()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        when (getCurrentAppTheme()) {
            //Дневной режим
            Configuration.UI_MODE_NIGHT_NO -> {
                menu.findItem(R.id.changeNightMode).apply {
                    setIcon(R.drawable.toolbar_moon_icon)
                    setTitle(R.string.toolbar_menu_set_dark_theme)
                }
            }
            //Ночной режим
            Configuration.UI_MODE_NIGHT_YES -> {
                menu.findItem(R.id.changeNightMode).apply {
                    setIcon(R.drawable.toolbar_sun_icon)
                    setTitle(R.string.toolbar_menu_set_light_theme)
                }
            }
            //по дефолту ночной режим
            else -> {
                menu.findItem(R.id.changeNightMode).apply {
                    setIcon(R.drawable.toolbar_moon_icon)
                    setTitle(R.string.toolbar_menu_set_dark_theme)
                }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.changeNightMode -> {
                changeAppTheme()
            }
            R.id.settings -> {
                showSettingsFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                sessionManager.storeSessionToken(token.accessToken)
                Log.e("mainActivity", "token:${token.accessToken}")
                VkTokenInterceptor.vkToken = token.accessToken
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
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, LikedPostsFragment.newInstance(), LIKED_POSTS_LIST)
            .commit()
    }

    private fun showAllPostsFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, PostsFeedFragment.newInstance(), ALL_POSTS_LIST)
            .commit()
    }

    fun showUserProfileFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, UserProfileFragment.newInstance())
            .commit()
    }

    override fun showNewPostFragment(vkUserId: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, SendPostFragment.newInstance(vkUserId))
            .addToBackStack(null)
            .commit()
    }

    override fun showSettingsFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, AppSettingsFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun showInitLoadingFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, InitLoadingFragment.newInstance(), INIT_LOADING)
            .commit()
    }

    fun hideGlobalToolbar() {
        if (appGlobalToolbar != null) {
            appGlobalToolbar.isGone = true
        }
    }

    fun showGlobalToolbar() {
        if (appGlobalToolbar != null) {
            appGlobalToolbar.isVisible = true
        }
    }

    fun hideBottomSettings() {
        if (postsBottomNavigation != null) {
            postsBottomNavigation.isVisible = false
        }
    }

    fun showBottomSettings() {
        if (postsBottomNavigation != null) {
            postsBottomNavigation.isVisible = true
        }
    }

    override fun onDestroy() {
        (applicationContext as ApplicationClass).clearMainActivityComponent()
        super.onDestroy()
    }

    private fun openVkLogin() {
        VK.login(this, arrayListOf(VKScope.WALL, VKScope.FRIENDS))
    }

    fun showVkLoginErrorAlert() {
        MaterialAlertDialogBuilder(this, R.style.AlertDialogStyle)
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

    private fun showNoNetworkNoVkTokenAlert(){
        MaterialAlertDialogBuilder(this, R.style.AlertDialogStyle)
            .setTitle(getString(R.string.vk_login_alert_dialog_title_text))
            .setMessage(getString(R.string.vk_login_alert_dialog_no_network_no_token_text))
            .setCancelable(false)
            .setPositiveButton(
                R.string.vk_login_alert_dialog_positive_button_text
            ) { _, _ ->
                this@MainActivity.finish()
            }
            .create().show()
    }

    override fun openPostDetails(post: PostData) {
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

    private fun setAppTheme() {
        val defaultSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        val isAppThemeForceSaved = defaultSharedPreferences.getBoolean(PREF_SAVE_APP_THEME, false)
        //если в настройках явно не указано, что надо запомнить тему - следим за темой, предоставляемой
        //андроид системой
        if (!isAppThemeForceSaved) {
            Log.e("setAppTheme", "not_forced_1");
            return
        }
        val appThemeSharedPref =
            this@MainActivity.getSharedPreferences(APP_THEME_PREF_NAME, PREF_PRIVATE_MODE)

        when (appThemeSharedPref.getString(
            PREF_APP_THEME_SELECTED,
            PREF_APP_THEME_SELECTED_DUNNO
        )) {
            PREF_APP_THEME_SELECTED_DAY -> {
                Log.e("setAppTheme", "selected day set day");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            PREF_APP_THEME_SELECTED_NIGHT -> {
                Log.e("setAppTheme", "selected night set night");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                Log.e("setAppTheme", "selected dunno set dunno");
            }
        }
    }

    private fun getCurrentAppTheme(): Int {
        val defaultSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        val isAppThemeForceSaved = defaultSharedPreferences.getBoolean(PREF_SAVE_APP_THEME, false)
        Log.e("getCurrentNitMdForced", isAppThemeForceSaved.toString());
        if (!isAppThemeForceSaved) {
            return (resources.configuration.uiMode
                    and Configuration.UI_MODE_NIGHT_MASK)
        }

        val appThemeSharedPref =
            this@MainActivity.getSharedPreferences(APP_THEME_PREF_NAME, PREF_PRIVATE_MODE)
        val appThemeSelected =
            appThemeSharedPref.getString(PREF_APP_THEME_SELECTED, PREF_APP_THEME_SELECTED_DUNNO)
        return if (appThemeSelected == PREF_APP_THEME_SELECTED_DAY) {
            Log.e("getCurrentNightMode", "day");
            Configuration.UI_MODE_NIGHT_NO
        } else {
            Log.e("getCurrentNightMode", "night");
            Configuration.UI_MODE_NIGHT_YES
        }
    }

    private fun changeAppTheme() {
        val appThemeSharedPref =
            this@MainActivity.getSharedPreferences(APP_THEME_PREF_NAME, PREF_PRIVATE_MODE)
        val editor = appThemeSharedPref.edit()
        when (getCurrentAppTheme()) {
            //Дневной режим
            Configuration.UI_MODE_NIGHT_NO -> {
                Log.e("updateAppTHm", "setNight1");
                editor.clear()
                editor.putString(PREF_APP_THEME_SELECTED, PREF_APP_THEME_SELECTED_NIGHT)
                editor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            //Ночной режим
            Configuration.UI_MODE_NIGHT_YES -> {
                Log.e("updateAppTHm", "setDay2");
                editor.clear()
                editor.putString(PREF_APP_THEME_SELECTED, PREF_APP_THEME_SELECTED_DAY)
                editor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            //дневной режим
            else -> {
                Log.e("updateAppTHm", "setNight2");
                editor.clear()
                editor.putString(PREF_APP_THEME_SELECTED, PREF_APP_THEME_SELECTED_NIGHT)
                editor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

    }

    companion object {
        const val ALL_POSTS_LIST = "all_posts_list"
        const val LIKED_POSTS_LIST = "liked_posts_list"
        const val POST_DETAILS = "post_details"
        const val INIT_LOADING = "init_loading"
        const val APP_THEME_PREF_NAME = "APP_THEME_SHARED_PREF"
        const val PREF_PRIVATE_MODE = 0
        const val PREF_SAVE_APP_THEME = "saveAppTheme"
        const val PREF_APP_THEME_SELECTED = "appThemeSelected"
        const val PREF_APP_THEME_SELECTED_DAY = "day"
        const val PREF_APP_THEME_SELECTED_NIGHT = "night"
        const val PREF_APP_THEME_SELECTED_DUNNO = "dunno"
    }
}
