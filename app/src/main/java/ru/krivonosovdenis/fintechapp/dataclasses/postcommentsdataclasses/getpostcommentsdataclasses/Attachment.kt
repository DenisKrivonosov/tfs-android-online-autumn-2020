package ru.krivonosovdenis.fintechapp.dataclasses.postcommentsdataclasses.getpostcommentsdataclasses


import com.google.gson.annotations.SerializedName

data class Attachment(
    val type: String,
    val photo: Photo,
    val sticker: Sticker
)