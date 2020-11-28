package ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses


import com.google.gson.annotations.SerializedName

data class PhotoXX(
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
    val userId: Int
)