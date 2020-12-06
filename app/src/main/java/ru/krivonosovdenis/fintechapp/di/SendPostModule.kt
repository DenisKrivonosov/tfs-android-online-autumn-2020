package ru.krivonosovdenis.fintechapp.di

import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.ui.sendpost.SendPostPresenter

@Module
class SendPostModule {
    @Provides
    fun provideSendPostPresenter(repository: Repository): SendPostPresenter {
        return SendPostPresenter(repository)
    }
}
