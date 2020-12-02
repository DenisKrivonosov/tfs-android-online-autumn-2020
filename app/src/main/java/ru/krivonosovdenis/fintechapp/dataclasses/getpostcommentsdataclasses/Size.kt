package ru.krivonosovdenis.fintechapp.dataclasses.getpostcommentsdataclasses


import com.google.gson.annotations.SerializedName

data class Size(
    val height: Int,
    val url: String,
    val type: String,
    val width: Int
)