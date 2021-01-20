package ru.krivonosovdenis.fintechapp.dataclasses.postcommentsdataclasses.getpostcommentsdataclasses

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
    val sizes: List<Size>,
    val text: String,
    @SerializedName("user_id")
    val userId: Int
)
