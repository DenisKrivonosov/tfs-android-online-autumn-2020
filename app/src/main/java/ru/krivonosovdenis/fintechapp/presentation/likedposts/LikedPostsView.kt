package ru.krivonosovdenis.fintechapp.presentation.likedposts

import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass

interface LikedPostsView {
    fun showPosts(posts: List<InfoRepresentationClass>)

    fun showPostsView()

    fun showErrorView()

    fun showGetPostErrorDialog()
}
