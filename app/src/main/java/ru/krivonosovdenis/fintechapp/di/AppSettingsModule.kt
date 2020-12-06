package ru.krivonosovdenis.fintechapp.di

import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.ui.appsettings.AppSettingsPresenter

@Module
class AppSettingsModule {
    @Provides
    fun provideAppSettingsPresenter(): AppSettingsPresenter {
        return AppSettingsPresenter()
    }
}
