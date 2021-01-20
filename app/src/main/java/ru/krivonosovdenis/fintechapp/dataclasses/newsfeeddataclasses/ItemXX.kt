package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class ItemXX(
    val id: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    val type: Int,
    val title: String,
    val description: String,
    val count: Int,
    val followers: Int,
    val plays: Int,
    @SerializedName("create_time")
    val createTime: Int,
    @SerializedName("update_time")
    val updateTime: Int,
    val genres: List<Genre>,
    @SerializedName("is_following")
    val isFollowing: Boolean,
    val year: Int,
    val original: Original,
    val photo: PhotoXX,
    @SerializedName("access_key")
    val accessKey: String,
    @SerializedName("is_explicit")
    val isExplicit: Boolean,
    @SerializedName("main_artists")
    val mainArtists: List<MainArtist>,
    @SerializedName("album_type")
    val albumType: String
)
