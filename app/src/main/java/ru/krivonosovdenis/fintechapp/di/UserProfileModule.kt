package ru.krivonosovdenis.fintechapp.di

import dagger.Module
import dagger.Provides
import ru.krivonosovdenis.fintechapp.data.Repository
import ru.krivonosovdenis.fintechapp.ui.userprofile.UserProfilePresenter

@Module
class UserProfileModule {
    @Provides
    fun provideUserProfilePresenter(repository: Repository): UserProfilePresenter {
        return UserProfilePresenter(repository)
    }
}
