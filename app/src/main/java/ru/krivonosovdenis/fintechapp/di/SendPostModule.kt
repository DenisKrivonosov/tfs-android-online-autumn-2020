package ru.krivonosovdenis.fintechapp.di

import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsPresenter
import ru.krivonosovdenis.fintechapp.presentation.postsfeed.PostsFeedPresenter
import ru.krivonosovdenis.fintechapp.presentation.sendpost.SendPostPresenter

@Module
class SendPostModule {
    @Provides
    fun provideSendPostPresenter(repository: Repository): SendPostPresenter {
        return SendPostPresenter(repository)
    }
}