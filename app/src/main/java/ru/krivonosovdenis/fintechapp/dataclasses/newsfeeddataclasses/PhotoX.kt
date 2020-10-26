package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class PhotoX(
    @SerializedName("album_id")
    val albumId: Int,
    val date: Int,
    val id: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    @SerializedName("has_tags")
    val hasTags: Boolean,
    val sizes: List<SizeX>,
    val text: String,
    @SerializedName("user_id")
    val userId: Int
)
