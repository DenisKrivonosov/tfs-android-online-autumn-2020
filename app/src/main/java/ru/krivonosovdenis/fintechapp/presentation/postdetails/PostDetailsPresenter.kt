package ru.krivonosovdenis.fintechapp.presentation.postdetails

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.RxPresenter

class PostDetailsPresenter(
    private val repository: Repository
) : RxPresenter<PostDetailsView>(PostDetailsView::class.java) {

    fun getPostFromDb(postId: Int, sourceId: Int) {
        repository.getPostFromDb(postId, sourceId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    view.showPost(it)
                }, onError = {
                    view.showErrorView()
                }
            )
            .disposeOnFinish()
    }
}
