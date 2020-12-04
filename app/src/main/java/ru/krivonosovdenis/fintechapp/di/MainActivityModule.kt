package ru.krivonosovdenis.fintechapp.di

import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.presentation.appsettings.AppSettingsPresenter
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivityPresenter

@Module
class MainActivityModule {
    @Provides
    fun provideMainActivityPresenter(repository: Repository): MainActivityPresenter {
        return MainActivityPresenter(repository)
    }
}
