package ru.krivonosovdenis.fintechapp.dataclasses.postcommentsdataclasses.getpostcommentsdataclasses

import com.google.gson.annotations.SerializedName

data class Likes(
    val count: Int,
    @SerializedName("user_likes")
    val userLikes: Int,
    @SerializedName("can_like")
    val canLike: Int,
    @SerializedName("can_publish")
    val canPublish: Boolean
)
