package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("access_key")
    val accessKey: String,
    @SerializedName("can_comment")
    val canComment: Int,
    @SerializedName("can_like")
    val canLike: Int,
    @SerializedName("can_repost")
    val canRepost: Int,
    @SerializedName("can_subscribe")
    val canSubscribe: Int,
    @SerializedName("can_add_to_faves")
    val canAddToFaves: Int,
    @SerializedName("can_add")
    val canAdd: Int,
    val comments: Int,
    val date: Int,
    val description: String,
    val duration: Int,
    val image: List<Image>,
    @SerializedName("first_frame")
    val firstFrame: List<FirstFrame>,
    val width: Int,
    val height: Int,
    val id: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    val title: String,
    @SerializedName("is_favorite")
    val isFavorite: Boolean,
    @SerializedName("track_code")
    val trackCode: String,
    val type: String,
    val views: Int
)
