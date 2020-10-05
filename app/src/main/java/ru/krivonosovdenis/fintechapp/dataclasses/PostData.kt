package ru.krivonosovdenis.fintechapp.dataclasses

data class PostData(
    val sourceId: Int,
    val postId: Int,
    val date: Long,
    val text: String,
    val photo: String?
)
