package ru.krivonosovdenis.fintechapp.presentation.sendpost

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.BaseRxPresenter

@InjectViewState
class SendPostPresenter(
    private val repository: Repository
) : BaseRxPresenter<SendPostView>() {

    fun getUserDataFromDb(vkUserId: Int) {
        repository.getUserInfoFromDb(vkUserId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    viewState.showSendPostEditorView(it)
                }, onError = {
                    viewState.showSendPostEditorInitErrorView()
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
                    viewState.showSendPostSuccessView()
                }, onError = {
                    viewState.showSendPostErrorView()
                }
            )
            .disposeOnFinish()
    }
}
