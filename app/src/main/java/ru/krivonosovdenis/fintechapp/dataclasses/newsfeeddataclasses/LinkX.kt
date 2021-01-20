package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class LinkX(
    val url: String,
    val title: String,
    val description: String,
    val target: String,
    val photo: PhotoXXXX,
    @SerializedName("is_favorite")
    val isFavorite: Boolean
)
