package ru.krivonosovdenis.fintechapp.dataclasses.getpostcommentsdataclasses


import com.google.gson.annotations.SerializedName

data class Image(
    val url: String,
    val width: Int,
    val height: Int
)