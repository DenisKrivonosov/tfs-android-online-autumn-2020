package ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses

import com.google.gson.annotations.SerializedName

data class Donut(
    @SerializedName("is_donut")
    val isDonut: Boolean
)
