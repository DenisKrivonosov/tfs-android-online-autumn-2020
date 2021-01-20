package ru.krivonosovdenis.fintechapp.dataclasses.newsfeeddataclasses

data class Attachment(
    val type: String,
    val photo: Photo?,
    val doc: Doc?,
    val link: Link?,
    val video: Video?
)
