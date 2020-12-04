package ru.krivonosovdenis.fintechapp.presentation.mainactivity

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.krivonosovdenis.fintechapp.dataclasses.PostData

interface MainActivityView:MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openPostDetails(post: PostData)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showBottomNavigationTabs()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setLikedPostsVisibility(visibilityFlag: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showNewPostFragment(vkUserId:Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSettingsFragment()
}
