package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Reposts(
    val count: Int,
    @SerializedName("user_reposted")
    val userReposted: Int
)
