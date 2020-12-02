package ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userwallpostsdataclasses


import com.google.gson.annotations.SerializedName

data class Item(
    val id: Int,
    @SerializedName("from_id")
    val fromId: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    val date: Int,
    @SerializedName("post_type")
    val postType: String,
    val text: String?,
    @SerializedName("can_edit")
    val canEdit: Int,
    @SerializedName("can_delete")
    val canDelete: Int,
    @SerializedName("can_pin")
    val canPin: Int,
    @SerializedName("can_archive")
    val canArchive: Boolean,
    @SerializedName("is_archived")
    val isArchived: Boolean,
    @SerializedName("post_source")
    val postSource: PostSource,
    val comments: Comments,
    val likes: Likes,
    val reposts: Reposts,
    @SerializedName("is_favorite")
    val isFavorite: Boolean,
    val donut: Donut,
    @SerializedName("short_text_rate")
    val shortTextRate: Double,
    val attachments: List<Attachment>?,
    val views: Views?,
    @SerializedName("copy_history")
    val copyHistory: List<CopyHistory>
)