package ru.krivonosovdenis.fintechapp.dataclasses.getpostcommentsdataclasses


import com.google.gson.annotations.SerializedName

data class Response(
    val count: Int,
    val items: List<Item>,
    val profiles: List<Profile>,
    val groups: List<Any>,
    @SerializedName("current_level_count")
    val currentLevelCount: Int,
    @SerializedName("can_post")
    val canPost: Boolean,
    @SerializedName("show_reply_button")
    val showReplyButton: Boolean
)