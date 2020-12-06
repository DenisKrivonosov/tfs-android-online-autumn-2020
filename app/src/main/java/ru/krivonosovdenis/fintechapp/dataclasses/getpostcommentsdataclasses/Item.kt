package ru.krivonosovdenis.fintechapp.dataclasses.getpostcommentsdataclasses

import com.google.gson.annotations.SerializedName

data class Item(
    val id: Int,
    @SerializedName("from_id")
    val fromId: Int,
    @SerializedName("post_id")
    val postId: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    @SerializedName("parents_stack")
    val parentsStack: List<Any>,
    val date: Int,
    val text: String?,
    val likes: Likes?,
    val thread: Thread,
    val attachments: List<Attachment>?
)
