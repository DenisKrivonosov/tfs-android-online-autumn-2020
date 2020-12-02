package ru.krivonosovdenis.fintechapp.presentation.postdetails

import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

interface PostDetailsView {
    fun renderPostDetails(post: PostFullData)

    fun renderPostComments(comments:List<CommentData>)

    fun showPostView()

    fun showDbLoadingErrorView()

    fun setRefreshing(isRefreshing: Boolean)

    fun showLoadDataFromNetworkErrorView()
}
