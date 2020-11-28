package ru.krivonosovdenis.fintechapp.presentation.likedposts

interface LikedPostsView {
    fun showPosts(posts: List<PostFullData>)

    fun showPostsView()

    fun showErrorView()

    fun showGetPostErrorDialog()
}
