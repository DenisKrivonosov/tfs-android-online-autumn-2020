package ru.krivonosovdenis.fintechapp.dataclasses.sendcommentdataclasses

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("comment_id")
    val commentId: Int,
    @SerializedName("parents_stack")
    val parentsStack: List<Any>
)
