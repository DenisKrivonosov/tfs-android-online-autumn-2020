package ru.krivonosovdenis.fintechapp.interfaces

import ru.krivonosovdenis.fintechapp.dataclasses.PostData

interface CommonAdapterActions {
    fun onPostDismiss(post: PostData)
    fun onPostLiked(post:PostData)
    fun onPostDisliked(post:PostData)
    fun onPostClicked(post: PostData)
}
