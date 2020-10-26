package ru.krivonosovdenis.fintechapp

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import ru.krivonosovdenis.fintechapp.networkutils.VkApiClient

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
        VkApiClient.accessToken = SessionManager(applicationContext).getToken()
    }

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            //Скорее всего эта штука не работает при текущей логике реализации запросов
            //По идее надо парсить ответ
        }
    }
}
