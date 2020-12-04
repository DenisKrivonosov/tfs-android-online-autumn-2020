package ru.krivonosovdenis.fintechapp.di

import dagger.Component
import ru.krivonosovdenis.fintechapp.presentation.appsettings.AppSettingsFragment
import ru.krivonosovdenis.fintechapp.presentation.likedposts.LikedPostsFragment

@Component(dependencies = [AppComponent::class],modules = [AppSettingsModule::class])
@AppSettingsScope
interface AppSettingsComponent {
    fun inject(appSettingsFragment: AppSettingsFragment)
}
