package ru.krivonosovdenis.fintechapp.presentation.base.mvp

import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.Presenter

interface MvpViewCallback<View, P : Presenter<View>> {

    fun getPresenter(): P

    fun getMvpView(): View

}
