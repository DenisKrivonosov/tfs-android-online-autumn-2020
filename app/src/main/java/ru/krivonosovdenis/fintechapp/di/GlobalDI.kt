package ru.krivonosovdenis.fintechapp.di

import android.content.Context
import android.content.res.Resources
import ru.krivonosovdenis.fintechapp.SessionManager
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.data.db.ApplicationDatabase
import ru.krivonosovdenis.fintechapp.data.network.VkApiClient
import ru.krivonosovdenis.fintechapp.presentation.postsfeed.PostsFeedPresenter
import ru.krivonosovdenis.fintechapp.presentation.appsettings.AppSettingsPresenter
import ru.krivonosovdenis.fintechapp.presentation.likedposts.LikedPostsPresenter
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivityPresenter
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsPresenter
import ru.krivonosovdenis.fintechapp.presentation.sendpost.SendPostPresenter
import ru.krivonosovdenis.fintechapp.presentation.userprofile.UserProfilePresenter

class GlobalDI private constructor(
    private val applicationContext: Context
) {
    var isFirstAllPostsFragmentOpen = true
    var isFirstUserProfileFragmentOpen = true
    val sessionManager by lazy { SessionManager(applicationContext) }
    val authNetworkClient by lazy { VkApiClient.getAuthRetrofitClient() }
    val dbConnection by lazy { ApplicationDatabase.getInstance(applicationContext) }

    val repository by lazy { Repository(authNetworkClient, dbConnection) }

    val appLanguage by lazy {
        Resources.getSystem().configuration.locale.language
    }

    val allPostsPresenter by lazy {
        PostsFeedPresenter(repository)
    }

    val allLikedPostsPresenter by lazy {
        LikedPostsPresenter(repository)
    }
    val postDetailsPresenter by lazy {
        PostDetailsPresenter(repository)
    }

    val userProfilePresenter by lazy {
        UserProfilePresenter(repository)
    }

    val sendPostPresenter by lazy {
        SendPostPresenter(repository)
    }

    val appSettingsPresenter by lazy {
        AppSettingsPresenter(repository)
    }

    val mainActivityPresenter by lazy {
        MainActivityPresenter(repository)
    }

    companion object {

        lateinit var INSTANCE: GlobalDI

        fun init(applicationContext: Context) {
            INSTANCE = GlobalDI(applicationContext)
        }
    }
}
