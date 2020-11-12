package ru.krivonosovdenis.fintechapp

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import ru.krivonosovdenis.fintechapp.data.db.ApplicationDatabase
import ru.krivonosovdenis.fintechapp.data.network.VkApiClient
import ru.krivonosovdenis.fintechapp.di.GlobalDI

class ApplicationClass : Application() {
    lateinit var appDataBase: ApplicationDatabase
    override fun onCreate() {
        super.onCreate()
        instance = this
        GlobalDI.init(this)

        appDataBase = ApplicationDatabase.getInstance(applicationContext)
        VK.addTokenExpiredHandler(tokenTracker)
        VkApiClient.accessToken = SessionManager(applicationContext).getToken()
    }

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            //Скорее всего эта штука не работает при текущей логике реализации запросов
            //По идее надо парсить ответ
        }
    }

    companion object {
        lateinit var instance: ApplicationClass
            private set
    }
}
