package ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses


import com.google.gson.annotations.SerializedName

data class Reposts(
    val count: Int,
    @SerializedName("wall_count")
    val wallCount: Int,
    @SerializedName("mail_count")
    val mailCount: Int,
    @SerializedName("user_reposted")
    val userReposted: Int
)