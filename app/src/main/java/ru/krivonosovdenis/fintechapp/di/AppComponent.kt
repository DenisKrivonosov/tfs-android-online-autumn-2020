package ru.krivonosovdenis.fintechapp.di

import android.net.ConnectivityManager
import dagger.Component
import ru.krivonosovdenis.fintechapp.ApplicationClass
import ru.krivonosovdenis.fintechapp.SessionManager
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.data.db.ApplicationDatabase
import ru.krivonosovdenis.fintechapp.data.network.VkApiClient
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(applicationClass: ApplicationClass)

    fun providesVkApiClient(): VkApiClient
    fun providesApplicationDataBase(): ApplicationDatabase
    fun providesRepository():Repository
    fun providesSessionManager():SessionManager
    fun providesConnectivityManager():ConnectivityManager

}
