package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

import com.google.gson.annotations.SerializedName

data class Original(
    @SerializedName("playlist_id")
    val playlistId: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    @SerializedName("access_key")
    val accessKey: String
)
