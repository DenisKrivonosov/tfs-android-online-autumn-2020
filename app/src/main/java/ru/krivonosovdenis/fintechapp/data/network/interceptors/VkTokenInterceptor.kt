package ru.krivonosovdenis.fintechapp.data.network.interceptors

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.krivonosovdenis.fintechapp.data.network.VkApiClient

class VkTokenInterceptor : Interceptor {
    /**
     * Add VK Token Interceptor
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        val url: HttpUrl = request.url.newBuilder()
            .addQueryParameter("access_token", VkApiClient.accessToken).build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}
