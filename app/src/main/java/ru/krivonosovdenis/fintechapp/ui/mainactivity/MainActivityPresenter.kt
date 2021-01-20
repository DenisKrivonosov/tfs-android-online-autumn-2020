package ru.krivonosovdenis.fintechapp.ui.mainactivity

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.ui.base.mvp.presenter.BaseRxPresenter

@InjectViewState
class MainActivityPresenter(
    private val repository: Repository
) : BaseRxPresenter<MainActivityView>() {

    fun subscribeBottomTabsOnDb() {
        repository.getLikedPostsCount()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    viewState.setLikedPostsVisibility(it.count() > 0)
                }
            )
            .disposeOnFinish()
    }

}
