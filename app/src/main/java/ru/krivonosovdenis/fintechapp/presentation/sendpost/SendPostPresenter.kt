package ru.krivonosovdenis.fintechapp.presentation.sendpost

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.RxPresenter
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsView

class SendPostPresenter(
    private val repository: Repository
) : RxPresenter<SendPostView>(SendPostView::class.java)  {

    fun getUserDataFromDb(vkUserId: Int) {
        repository.getUserInfoFromDb(vkUserId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    view.showSendPostEditorView(it)
                }, onError = {
                    view.showSendPostEditorInitErrorView()
                }
            )
            .disposeOnFinish()
    }

    fun sendPostToBack(text:String,attachments:String){
        repository.sendPostToOwnWall(text,attachments)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    view.showSendPostSuccessView()
                }, onError = {
                    view.showSendPostErrorView()
                }
            )
            .disposeOnFinish()
    }
}
