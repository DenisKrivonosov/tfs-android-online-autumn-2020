package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class OnlineInfo(
    val visible: Boolean,
    @SerializedName("is_online")
    val isOnline: Boolean,
    @SerializedName("is_mobile")
    val isMobile: Boolean,
    @SerializedName("last_seen")
    val lastSeen: Int,
    @SerializedName("app_id")
    val appId: Int
)
