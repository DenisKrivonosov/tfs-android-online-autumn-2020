package ru.krivonosovdenis.fintechapp.interfaces

import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

interface AllPostsActions {
    fun onPostDismiss(post: PostFullData)
    fun onPostLiked(post:PostFullData)
    fun onPostClicked(post: PostFullData)
}
