package ru.krivonosovdenis.fintechapp.ui.base.mvp.presenter

import androidx.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpPresenter
import moxy.MvpView

open class BaseRxPresenter<view : MvpView> : MvpPresenter<view>() {
    private val disposables = CompositeDisposable()

    protected fun Disposable.disposeOnFinish(): Disposable {
        disposables.add(this)
        return this
    }

    @CallSuper
    override fun onDestroy() {
        disposables.clear()
    }
}
