package ru.krivonosovdenis.fintechapp.presentation.mainactivity

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.RxPresenter

class MainActivityPresenter(
    private val repository: Repository
) : RxPresenter<MainActivityView>(MainActivityView::class.java) {

    fun subscribeBottomTabsOnDb() {
        repository.getLikedPostsCount()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    view.setLikedPostsVisibility(it > 0)
                }
            )
            .disposeOnFinish()
    }

}
