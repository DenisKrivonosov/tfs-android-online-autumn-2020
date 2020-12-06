package ru.krivonosovdenis.fintechapp.di

import dagger.Component
import ru.krivonosovdenis.fintechapp.ui.appsettings.AppSettingsFragment

@Component(dependencies = [AppComponent::class], modules = [AppSettingsModule::class])
@AppSettingsScope
interface AppSettingsComponent {
    fun inject(appSettingsFragment: AppSettingsFragment)
}
