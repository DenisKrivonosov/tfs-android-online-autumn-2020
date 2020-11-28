package ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses


import com.google.gson.annotations.SerializedName

data class SizeX(
    val height: Int,
    val url: String,
    val type: String,
    val width: Int
)