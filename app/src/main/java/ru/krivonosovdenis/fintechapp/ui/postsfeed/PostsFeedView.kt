package ru.krivonosovdenis.fintechapp.ui.postsfeed

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.krivonosovdenis.fintechapp.dataclasses.PostData

interface PostsFeedView : MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPosts(posts: List<PostData>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPostsView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDbGetFeedErrorView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPostUpdateErrorToast()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showLoadingView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setRefreshing(isRefreshing: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showLoadDataFromNetworkErrorView()
}
