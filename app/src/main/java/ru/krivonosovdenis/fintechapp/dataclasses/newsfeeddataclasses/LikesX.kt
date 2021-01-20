package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class LikesX(
    @SerializedName("user_likes")
    val userLikes: Int,
    val count: Int
)
