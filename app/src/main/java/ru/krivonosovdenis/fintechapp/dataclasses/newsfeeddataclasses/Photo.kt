package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Photo(
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
    val sizes: List<Size>,
    val text: String,
    @SerializedName("user_id")
    val userId: Int,
    val lat: Double,
    val long: Double
)
