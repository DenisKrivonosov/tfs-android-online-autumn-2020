package ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter

interface Presenter<View> {

    fun attachView(view: View)

    fun detachView(isFinishing: Boolean)
}
