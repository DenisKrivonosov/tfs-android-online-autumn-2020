package ru.krivonosovdenis.fintechapp.presentation.sendpost

import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.RxPresenter
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsView

class SendPostPresenter(
    private val repository: Repository
) : RxPresenter<SendPostView>(SendPostView::class.java)  {
}