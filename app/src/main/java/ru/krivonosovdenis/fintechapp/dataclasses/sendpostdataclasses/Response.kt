package ru.krivonosovdenis.fintechapp.dataclasses.sendpostdataclasses

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("post_id")
    val postId: Int
)
