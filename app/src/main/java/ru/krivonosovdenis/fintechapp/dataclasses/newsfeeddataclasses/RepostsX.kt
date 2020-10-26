package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class RepostsX(
    val count: Int,
    @SerializedName("user_reposted")
    val userReposted: Int
)
