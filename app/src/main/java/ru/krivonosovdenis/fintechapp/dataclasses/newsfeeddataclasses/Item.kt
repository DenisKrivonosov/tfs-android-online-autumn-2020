package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("source_id")
    val sourceId: Int,
    val date: Int,
    @SerializedName("can_doubt_category")
    val canDoubtCategory: Boolean,
    @SerializedName("can_set_category")
    val canSetCategory: Boolean,
    @SerializedName("post_type")
    val postType: String,
    val text: String?,
    @SerializedName("marked_as_ads")
    val markedAsAds: Int,
    val attachments: List<Attachment>?,
    @SerializedName("post_source")
    val postSource: PostSource,
    val comments: Comments?,
    val likes: Likes?,
    val reposts: Reposts?,
    val views: Views?,
    @SerializedName("is_favorite")
    val isFavorite: Boolean,
    @SerializedName("post_id")
    val postId: Int,
    val type: String,
    val photos: Photos?,
    @SerializedName("audio_playlist")
    val audioPlaylist: AudioPlaylist,
    @SerializedName("copy_history")
    val copyHistory: List<CopyHistory>,
    val copyright: Copyright,
    @SerializedName("topic_id")
    val topicId: Int,
    @SerializedName("signer_id")
    val signerId: Int
)
