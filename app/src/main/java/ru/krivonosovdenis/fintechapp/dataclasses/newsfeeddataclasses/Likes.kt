package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Likes(
    val count: Int,
    @SerializedName("user_likes")
    val userLikes: Int,
    @SerializedName("can_like")
    val canLike: Int,
    @SerializedName("can_publish")
    val canPublish: Int
)
