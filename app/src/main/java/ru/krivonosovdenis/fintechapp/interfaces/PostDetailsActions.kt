package ru.krivonosovdenis.fintechapp.interfaces


import ru.krivonosovdenis.fintechapp.dataclasses.PostData

interface PostDetailsActions {
    fun sharePostImage(post: PostData)
    fun savePostImage(post:PostData)
    fun likePost(post:PostData)
    fun dislikePost(post:PostData)
}
