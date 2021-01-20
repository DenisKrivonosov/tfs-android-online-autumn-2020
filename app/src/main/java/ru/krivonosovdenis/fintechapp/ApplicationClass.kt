package ru.krivonosovdenis.fintechapp

import android.app.Application
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import ru.krivonosovdenis.fintechapp.di.*
import javax.inject.Inject

class ApplicationClass : Application() {
    var appComponent: AppComponent? = null
    var mainActivityComponent: MainActivityComponent? = null
    var postsFeedComponent: PostsFeedComponent? = null
    var likedPostsComponent: LikedPostsComponent? = null
    var postDetailsComponent: PostDetailsComponent? = null
    var userProfileComponent: UserProfileComponent? = null
    var sendPostComponent: SendPostComponent? = null
    var appSettingsComponent: AppSettingsComponent? = null

    var isNetworkAvailableVariable: Boolean = false

    private var networkCallback: NetworkCallback? = null

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().appModule(AppModule(applicationContext)).build()
        appComponent?.inject(this)
        registerConnectivityMonitoring()
        VK.addTokenExpiredHandler(tokenTracker)
    }

    private fun registerConnectivityMonitoring() {
        val networkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    isNetworkAvailableVariable = true
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    isNetworkAvailableVariable = false
                }

            }
        this.networkCallback = networkCallback
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            networkCallback
        )
    }


    fun addMainActivityComponent() {
        mainActivityComponent = DaggerMainActivityComponent.builder().appComponent(appComponent)
            .build()
    }

    fun clearMainActivityComponent() {
        mainActivityComponent = null
    }


    fun addPostsFeedComponent() {
        postsFeedComponent = DaggerPostsFeedComponent.builder().appComponent(appComponent)
            .build()
    }

    fun clearPostsFeedComponent() {
        postsFeedComponent = null
    }

    fun addLikedPostsComponent() {
        likedPostsComponent = DaggerLikedPostsComponent.builder().appComponent(appComponent)
            .build()
    }

    fun clearLikedPostsComponent() {
        likedPostsComponent = null
    }

    fun addPostDetailsComponent() {
        postDetailsComponent = DaggerPostDetailsComponent.builder().appComponent(appComponent)
            .build()
    }

    fun clearPostDetailsComponent() {
        postDetailsComponent = null
    }

    fun addUserProfileComponent() {
        userProfileComponent = DaggerUserProfileComponent.builder().appComponent(appComponent)
            .build()
    }

    fun clearUserProfileComponent() {
        userProfileComponent = null
    }

    fun addSendPostComponent() {
        sendPostComponent = DaggerSendPostComponent.builder().appComponent(appComponent)
            .build()
    }

    fun clearSendPostComponent() {
        sendPostComponent = null
    }

    fun addAppSettingsComponent() {
        appSettingsComponent = DaggerAppSettingsComponent.builder().appComponent(appComponent)
            .build()
    }

    fun clearAppSettingsComponent() {
        appSettingsComponent = null
    }

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
        }
    }

}
