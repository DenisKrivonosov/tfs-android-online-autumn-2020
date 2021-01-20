package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Link(
    val url: String,
    val title: String,
    val caption: String,
    val description: String,
    val photo: PhotoX,
    @SerializedName("is_favorite")
    val isFavorite: Boolean,
    val target: String
)
