package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Profile(
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val deactivated: String,
    @SerializedName("photo_50")
    val photo50: String,
    @SerializedName("photo_100")
    val photo100: String,
    val sex: Int,
    val online: Int,
    @SerializedName("online_info")
    val onlineInfo: OnlineInfo,
    @SerializedName("is_closed")
    val isClosed: Boolean,
    @SerializedName("can_access_closed")
    val canAccessClosed: Boolean,
    @SerializedName("screen_name")
    val screenName: String
)
