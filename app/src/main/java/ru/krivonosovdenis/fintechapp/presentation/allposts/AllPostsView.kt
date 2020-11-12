package ru.krivonosovdenis.fintechapp.presentation.allposts

import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

interface AllPostsView {
    fun showPosts(posts: List<PostFullData>)

    fun showPostsView()

    fun showErrorView()

    fun showLoadingView()

    fun setRefreshing(isRefreshing: Boolean)

    fun scrollViewToTop()
}
