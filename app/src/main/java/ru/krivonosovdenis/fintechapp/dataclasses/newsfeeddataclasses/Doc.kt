package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Doc(
    val id: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    val title: String,
    val size: Int,
    val ext: String,
    val date: Int,
    val type: Int,
    val url: String,
    @SerializedName("access_key")
    val accessKey: String
)
