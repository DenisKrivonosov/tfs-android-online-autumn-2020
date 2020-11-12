package ru.krivonosovdenis.fintechapp.presentation.likedposts

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.RxPresenter

class LikedPostsPresenter(
    private val repository: Repository
) : RxPresenter<LikedPostsView>(LikedPostsView::class.java) {

    fun subscribeOnAllLikedPostsFromDB() {
        repository.getLikedPostsFromDb()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    if (it.count() > 0) {
                        view.showPosts(it)
                    }
                },
                onError = {
                    view.showGetPostErrorDialog()
                    view.showErrorView()
                }
            )
            .disposeOnFinish()
    }

}
