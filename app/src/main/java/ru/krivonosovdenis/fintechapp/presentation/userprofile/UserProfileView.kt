package ru.krivonosovdenis.fintechapp.presentation.userprofile

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileData

interface UserProfileView:MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setRefreshing(isRefreshing: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun scrollViewToTop()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showProfileInfo()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showLoadingView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showErrorView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showLoadDataFromNetworkErrorView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun renderProfileAndShow(userInfoData:UserProfileData)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun renderUserPostsAndShow(posts:List<PostData>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPostUpdateErrorToast()

}