package ru.krivonosovdenis.fintechapp.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.NonNull
import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.SessionManager
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.data.db.ApplicationDatabase
import ru.krivonosovdenis.fintechapp.data.network.VkApiClient
import javax.inject.Singleton

@Module
class AppModule(var appContext: Context) {

    @Provides
    @NonNull
    fun provideContext(): Context {
        return appContext
    }

    @Provides
    @Singleton
    fun provideVkApiClient(): VkApiClient {
        return VkApiClient()
    }

    @Provides
    @Singleton
    fun provideDataBase(appContext: Context): ApplicationDatabase {
        return ApplicationDatabase.getInstance(appContext)
    }

    @Provides
    @Singleton
    fun provideRepository(vkApiClient: VkApiClient, dbConnect: ApplicationDatabase): Repository {
        return Repository(vkApiClient, dbConnect)
    }

    @Provides
    @Singleton
    fun provideSessionManager(appContext: Context): SessionManager {
        return SessionManager(appContext)
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(appContext: Context): ConnectivityManager {
        return appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

}
