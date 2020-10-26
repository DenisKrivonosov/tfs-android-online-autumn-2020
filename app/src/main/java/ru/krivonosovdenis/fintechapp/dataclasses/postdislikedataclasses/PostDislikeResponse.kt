package ru.krivonosovdenis.fintechapp.dataclasses.postdislikedataclasses

//удаление пос та из фида. ерспонс приходит 1 - удален, 0 - не удален
data class PostDislikeResponse(
    val response: Int
)
