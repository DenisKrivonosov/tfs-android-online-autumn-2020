package ru.krivonosovdenis.fintechapp.dataclasses.userprofiledataclasses.userfullinfodataclasses


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("first_name")
    val firstName: String,
    val id: Int,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("can_access_closed")
    val canAccessClosed: Boolean,
    @SerializedName("is_closed")
    val isClosed: Boolean,
    val domain: String,
    val bdate: String,
    val city: City,
    val country: Country,
    val photo: String,
    val about: String,
    @SerializedName("last_seen")
    val lastSeen: LastSeen,
    @SerializedName("followers_count")
    val followersCount: Int,
    val career: List<Any>,
    val university: Int,
    @SerializedName("university_name")
    val universityName: String,
    val faculty: Int,
    @SerializedName("faculty_name")
    val facultyName: String,
    val graduation: Int
)