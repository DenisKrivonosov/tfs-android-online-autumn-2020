package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Response(
    val items: List<Item>,
    val profiles: List<Profile>,
    val groups: List<Group>,
    @SerializedName("next_from")
    val nextFrom: String
)
