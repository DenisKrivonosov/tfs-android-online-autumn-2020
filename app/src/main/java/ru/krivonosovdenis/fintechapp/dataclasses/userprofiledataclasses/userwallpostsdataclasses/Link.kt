package ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses

import com.google.gson.annotations.SerializedName

data class Link(
    val url: String,
    val title: String,
    val caption: String,
    val description: String,
    val photo: Photo,
    @SerializedName("is_favorite")
    val isFavorite: Boolean
)
