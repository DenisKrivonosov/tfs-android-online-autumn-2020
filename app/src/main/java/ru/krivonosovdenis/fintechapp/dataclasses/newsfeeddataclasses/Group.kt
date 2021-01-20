package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Group(
    val id: Int,
    val name: String,
    @SerializedName("screen_name")
    val screenName: String,
    @SerializedName("is_closed")
    val isClosed: Int,
    val type: String,
    @SerializedName("photo_50")
    val photo50: String,
    @SerializedName("photo_100")
    val photo100: String,
    @SerializedName("photo_200")
    val photo200: String
)
