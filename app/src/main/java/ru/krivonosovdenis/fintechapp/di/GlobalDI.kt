package ru.krivonosovdenis.fintechapp.di

import android.content.Context
import ru.krivonosovdenis.fintechapp.SessionManager
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.data.db.ApplicationDatabase
import ru.krivonosovdenis.fintechapp.data.network.VkApiClient
import ru.krivonosovdenis.fintechapp.presentation.allposts.AllPostsPresenter
import ru.krivonosovdenis.fintechapp.presentation.likedposts.LikedPostsPresenter
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivityPresenter
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsPresenter

class GlobalDI private constructor(
    private val applicationContext: Context
) {
    var isFirstAllPostsFragmentOpen = true
    val sessionManager by lazy { SessionManager(applicationContext) }
    val authNetworkClient by lazy { VkApiClient.getAuthRetrofitClient() }
    val dbConnection by lazy { ApplicationDatabase.getInstance(applicationContext) }

    val repository by lazy { Repository(authNetworkClient, dbConnection) }

    val allPostsPresenter by lazy {
        AllPostsPresenter(repository)
    }

    val allLikedPostsPresenter by lazy {
        LikedPostsPresenter(repository)
    }
    val postDetailsPresenter by lazy {
        PostDetailsPresenter(repository)
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
