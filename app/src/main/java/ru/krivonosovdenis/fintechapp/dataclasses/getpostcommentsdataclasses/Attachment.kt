package ru.krivonosovdenis.fintechapp.dataclasses.getpostcommentsdataclasses

data class Attachment(
    val type: String,
    val photo: Photo?,
    val sticker: Sticker?
)
