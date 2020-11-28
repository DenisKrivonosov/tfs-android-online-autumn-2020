package ru.krivonosovdenis.fintechapp.dataclasses

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

sealed class InfoRepresentationClass

@Entity(tableName = "user_profile_info", primaryKeys = ["userId"])
@Parcelize
data class UserProfileMainInfo(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val bdate: String,
    val city: String?,
    var country: String?,
    var photo: String,
    var lastSeen: Int,
    var followersCount:Int,
    var universityName:String?,
    var facultyName:String?,
): InfoRepresentationClass(), Parcelable

@Entity(tableName = "posts", primaryKeys = ["postId", "sourceId"])
@Parcelize
data class PostFullData(
    val postId: Int,
    val sourceId: Int,
    val posterAvatar: String,
    val posterName: String,
    val date: DateTime,
    val text: String,
    val photo: String?,
    var likesCount: Int,
    var isLiked: Boolean = false,
    var commentsCount: Int,
    var repostsCount: Int,
    var viewsCount: Int,
    val postSource:Int
) :InfoRepresentationClass(), Parcelable
