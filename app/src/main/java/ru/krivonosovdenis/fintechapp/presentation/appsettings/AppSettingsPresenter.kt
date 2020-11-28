package ru.krivonosovdenis.fintechapp.presentation.appsettings

import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.RxPresenter
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsView

class AppSettingsPresenter(
    private val repository: Repository
) : RxPresenter<AppSettingsView>(AppSettingsView::class.java) {
}