package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Image(
    val height: Int,
    val url: String,
    val width: Int,
    @SerializedName("with_padding")
    val withPadding: Int
)
