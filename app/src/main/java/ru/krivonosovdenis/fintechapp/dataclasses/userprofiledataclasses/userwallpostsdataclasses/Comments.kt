package ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses

import com.google.gson.annotations.SerializedName

data class Comments(
    val count: Int,
    @SerializedName("can_post")
    val canPost: Int,
    @SerializedName("groups_can_post")
    val groupsCanPost: Boolean,
    @SerializedName("can_close")
    val canClose: Int
)
