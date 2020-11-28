package ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userfullinfodataclasses


import com.google.gson.annotations.SerializedName

data class LastSeen(
    val platform: Int,
    val time: Int
)