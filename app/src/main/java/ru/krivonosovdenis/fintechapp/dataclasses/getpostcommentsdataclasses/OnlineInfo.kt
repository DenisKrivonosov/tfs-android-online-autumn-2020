package ru.krivonosovdenis.fintechapp.dataclasses.getpostcommentsdataclasses


import com.google.gson.annotations.SerializedName

data class OnlineInfo(
    val visible: Boolean,
    @SerializedName("last_seen")
    val lastSeen: Int,
    @SerializedName("is_online")
    val isOnline: Boolean,
    @SerializedName("app_id")
    val appId: Int,
    @SerializedName("is_mobile")
    val isMobile: Boolean,
    val status: String
)