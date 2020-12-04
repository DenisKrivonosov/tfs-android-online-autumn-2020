package ru.krivonosovdenis.fintechapp.presentation.appsettings

import moxy.InjectViewState
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.BaseRxPresenter

@InjectViewState
class AppSettingsPresenter(
    private val repository: Repository
) : BaseRxPresenter<AppSettingsView>() {
}
