package ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses


import com.google.gson.annotations.SerializedName

data class Attachment(
    val type: String,
    val link: Link,
    val photo: PhotoX
)