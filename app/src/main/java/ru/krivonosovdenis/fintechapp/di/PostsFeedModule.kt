package ru.krivonosovdenis.fintechapp.di

import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.ui.postsfeed.PostsFeedPresenter

@Module
class PostsFeedModule() {
    @Provides
    fun providePostsFeedPresenter(repository: Repository): PostsFeedPresenter {
        return PostsFeedPresenter(repository)
    }
}
