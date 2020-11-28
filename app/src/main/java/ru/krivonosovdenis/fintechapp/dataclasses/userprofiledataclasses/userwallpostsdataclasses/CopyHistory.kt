package ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses


import com.google.gson.annotations.SerializedName

data class CopyHistory(
    val id: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    @SerializedName("from_id")
    val fromId: Int,
    val date: Int,
    @SerializedName("post_type")
    val postType: String,
    val text: String,
    val attachments: List<AttachmentX>,
    @SerializedName("post_source")
    val postSource: PostSourceX
)