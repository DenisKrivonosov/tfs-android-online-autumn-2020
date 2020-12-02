package ru.krivonosovdenis.fintechapp.interfaces

import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

interface CommonAdapterActions {
    fun onPostDismiss(post: PostFullData)
    fun onPostLiked(post:PostFullData)
    fun onPostDisliked(post:PostFullData)
    fun onPostClicked(post: PostFullData)
}
