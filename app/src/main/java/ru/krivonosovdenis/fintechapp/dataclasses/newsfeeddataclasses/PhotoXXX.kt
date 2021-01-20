package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class PhotoXXX(
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
    val sizes: List<SizeXXX>,
    val text: String,
    @SerializedName("user_id")
    val userId: Int
)
