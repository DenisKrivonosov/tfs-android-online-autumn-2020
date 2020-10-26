package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class ItemX(
    @SerializedName("album_id")
    val albumId: Int,
    val date: Int,
    val id: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    @SerializedName("has_tags")
    val hasTags: Boolean,
    @SerializedName("access_key")
    val accessKey: String,
    @SerializedName("post_id")
    val postId: Int,
    val sizes: List<SizeXX>,
    val text: String,
    @SerializedName("user_id")
    val userId: Int,
    val likes: LikesX,
    val reposts: RepostsX,
    val comments: CommentsX,
    @SerializedName("can_comment")
    val canComment: Int,
    @SerializedName("can_repost")
    val canRepost: Int,
    val lat: Double,
    val long: Double
)
