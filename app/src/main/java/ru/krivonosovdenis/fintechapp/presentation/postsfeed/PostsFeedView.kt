package ru.krivonosovdenis.fintechapp.presentation.postsfeed

import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

interface PostsFeedView {
    fun showPosts(posts: List<PostFullData>)

    fun showPostsView()

    fun showDbGetFeedErrorView()

    fun showPostUpdateErrorToast()

    fun showLoadingView()

    fun setRefreshing(isRefreshing: Boolean)

}
