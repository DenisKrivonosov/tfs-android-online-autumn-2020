package ru.krivonosovdenis.fintechapp.interfaces


import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

interface PostDetailsActions {
    fun sharePostImage(post: PostFullData)
    fun savePostImage(post:PostFullData)
}
