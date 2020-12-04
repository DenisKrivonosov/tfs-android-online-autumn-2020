package ru.krivonosovdenis.fintechapp.di

import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsPresenter
import ru.krivonosovdenis.fintechapp.presentation.postsfeed.PostsFeedPresenter

@Module
class PostDetailsModule {
    @Provides
    fun providePostDetailsPresenter(repository: Repository): PostDetailsPresenter {
        return PostDetailsPresenter(repository)
    }
}