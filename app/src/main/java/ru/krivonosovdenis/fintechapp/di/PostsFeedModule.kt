package ru.krivonosovdenis.fintechapp.di

import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.postsfeed.PostsFeedPresenter
import ru.krivonosovdenis.fintechapp.rvcomponents.CommonRVAdapter

@Module
class PostsFeedModule {
    @Provides
    fun providePostsFeedPresenter(repository:Repository):PostsFeedPresenter{
        return PostsFeedPresenter(repository)
    }

}