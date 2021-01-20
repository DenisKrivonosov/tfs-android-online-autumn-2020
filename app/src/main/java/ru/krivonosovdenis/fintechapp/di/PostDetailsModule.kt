package ru.krivonosovdenis.fintechapp.di

import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.ui.postdetails.PostDetailsPresenter

@Module
class PostDetailsModule {
    @Provides
    fun providePostDetailsPresenter(repository: Repository): PostDetailsPresenter {
        return PostDetailsPresenter(repository)
    }
}
