package ru.krivonosovdenis.fintechapp.ui.likedposts

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass

interface LikedPostsView : MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPosts(posts: List<InfoRepresentationClass>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPostsView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showErrorView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showEmptyLikedPostsView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showGetPostErrorDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPostUpdateErrorToast()
}
