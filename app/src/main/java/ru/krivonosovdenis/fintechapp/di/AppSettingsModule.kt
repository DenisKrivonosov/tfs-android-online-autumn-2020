package ru.krivonosovdenis.fintechapp.di

import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.appsettings.AppSettingsPresenter
import ru.krivonosovdenis.fintechapp.presentation.likedposts.LikedPostsPresenter

@Module
class AppSettingsModule {
    @Provides
    fun provideAppSettingsPresenter(repository: Repository): AppSettingsPresenter {
        return AppSettingsPresenter(repository)
    }
}