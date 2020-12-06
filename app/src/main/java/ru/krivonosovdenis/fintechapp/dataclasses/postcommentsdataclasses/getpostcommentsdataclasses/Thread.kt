package ru.krivonosovdenis.fintechapp.dataclasses.postcommentsdataclasses.getpostcommentsdataclasses

import com.google.gson.annotations.SerializedName

data class Thread(
    val count: Int,
    val items: List<Any>,
    @SerializedName("can_post")
    val canPost: Boolean,
    @SerializedName("show_reply_button")
    val showReplyButton: Boolean
)
