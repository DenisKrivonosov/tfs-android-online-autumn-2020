package ru.krivonosovdenis.fintechapp.interfaces

import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

interface LikedPostsActions {
    fun onPostClicked(post: PostFullData)
}