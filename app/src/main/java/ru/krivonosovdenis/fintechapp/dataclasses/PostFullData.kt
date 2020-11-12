package ru.krivonosovdenis.fintechapp.dataclasses

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@Entity(tableName = "all_feed_posts", primaryKeys = ["postId", "sourceId"])
@Parcelize
data class PostFullData(
    val postId: Int,
    val sourceId: Int,
    val groupAvatar: String,
    val groupName: String,
    val date: DateTime,
    val text: String,
    val photo: String?,
    var likesCount: Int,
    var isLiked: Boolean = false,
    var isCommented: Boolean = false,
    var isShared: Boolean = false,
    var isHidden: Boolean = false
) : Parcelable
