package ru.krivonosovdenis.fintechapp.dataclasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@Parcelize
data class PostRenderData(
    val postId: String,
    val groupAvatar: String,
    val groupName: String,
    val date: DateTime,
    val text: String,
    val photo: String?,
    var isLiked: Boolean = false,
    var isCommented: Boolean = false,
    var isShared: Boolean = false,
    var isHidden: Boolean = false
) : Parcelable
