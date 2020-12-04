package ru.krivonosovdenis.fintechapp.data.network.interceptors

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.krivonosovdenis.fintechapp.data.network.VkApiClient
import javax.inject.Inject
import javax.inject.Singleton

object VkTokenInterceptor: Interceptor {

    var vkToken:String? = null
    /**
     * Add VK Token Interceptor
     */

    init{
        Log.i("VkTokenInterceptor","VkTokenInterceptor");

    }
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.e("HEEEERREEE","123 ${vkToken}");
        var request: Request = chain.request()
        val url: HttpUrl = request.url.newBuilder()
            .addQueryParameter("access_token",  vkToken).build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}
