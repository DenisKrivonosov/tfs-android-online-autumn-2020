package ru.krivonosovdenis.fintechapp.dataclasses.getpostcommentsdataclasses

import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("first_name")
    val firstName: String,
    val id: Int,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("can_access_closed")
    val canAccessClosed: Boolean,
    @SerializedName("is_closed")
    val isClosed: Boolean,
    val sex: Int,
    @SerializedName("screen_name")
    val screenName: String,
    @SerializedName("photo_50")
    val photo50: String,
    @SerializedName("photo_100")
    val photo100: String,
    @SerializedName("online_info")
    val onlineInfo: OnlineInfo,
    val online: Int,
    @SerializedName("online_mobile")
    val onlineMobile: Int,
    @SerializedName("online_app")
    val onlineApp: Int
)
