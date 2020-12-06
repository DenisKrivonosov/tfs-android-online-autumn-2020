package ru.krivonosovdenis.fintechapp.ui.sendpost

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileData

interface SendPostView : MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSendPostEditorInitErrorView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSendPostEditorView(userData: UserProfileData)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSendPostSuccessView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSendPostErrorView()

}
