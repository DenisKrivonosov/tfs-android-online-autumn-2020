package ru.krivonosovdenis.fintechapp.di

import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.likedposts.LikedPostsPresenter
import ru.krivonosovdenis.fintechapp.presentation.postsfeed.PostsFeedPresenter

@Module
class LikedPostsModule {
    @Provides
    fun provideLikedPostsPresenter(repository: Repository): LikedPostsPresenter {
        return LikedPostsPresenter(repository)
    }
}