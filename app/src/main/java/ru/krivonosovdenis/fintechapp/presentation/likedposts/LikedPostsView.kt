package ru.krivonosovdenis.fintechapp.presentation.likedposts

import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

interface LikedPostsView {
    fun showPosts(posts: List<PostFullData>)

    fun showPostsView()

    fun showErrorView()

    fun showGetPostErrorDialog()
}
