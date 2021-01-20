package ru.krivonosovdenis.fintechapp.interfaces

import ru.krivonosovdenis.fintechapp.dataclasses.PostData

interface LikedPostsActions {
    fun onPostClicked(post: PostData)
}
