package ru.krivonosovdenis.fintechapp.presentation.postdetails

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.PostData

interface PostDetailsView:MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun renderPostDetails(postDetailsData: PostData)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun renderPostComments(comments:List<CommentData>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPostView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDbLoadingErrorView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setRefreshing(isRefreshing: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showLoadDataFromNetworkErrorView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPostUpdateErrorToast()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showCommentUpdateErrorToast()
}
