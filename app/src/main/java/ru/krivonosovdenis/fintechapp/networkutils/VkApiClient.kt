package ru.krivonosovdenis.fintechapp.networkutils

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.krivonosovdenis.fintechapp.networkutils.interceptors.VkApiVersionInterceptor
import ru.krivonosovdenis.fintechapp.networkutils.interceptors.VkTokenInterceptor
import java.util.concurrent.TimeUnit

object VkApiClient {

    private const val VK_URL: String = "https://api.vk.com/method/"
    var accessToken = ""
    const val VK_API_VERSION = "5.124"

    private val gson = GsonBuilder()
        .setLenient()
        .create()
    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

    private var authRetrofit: Retrofit? = null

    private fun getAuthHttpClient(): OkHttpClient {
        val loggerInterceptor = HttpLoggingInterceptor()
        loggerInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient()
            .newBuilder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggerInterceptor)
            .addInterceptor(loggerInterceptor)
            .addInterceptor(VkTokenInterceptor())
            .addInterceptor(VkApiVersionInterceptor())
            .build()
    }

    fun getAuthRetrofitClient(): ApiInterface {
        return if (authRetrofit == null) {
            Retrofit.Builder()
                .baseUrl(VK_URL)
                .client(getAuthHttpClient())
                .addConverterFactory(gsonFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(ApiInterface::class.java)
        } else {
            authRetrofit!!.create(ApiInterface::class.java)
        }
    }
}
