package ru.krivonosovdenis.fintechapp.di

import dagger.Component
import ru.krivonosovdenis.fintechapp.ui.userprofile.UserProfileFragment

@Component(dependencies = [AppComponent::class], modules = [UserProfileModule::class])
@UserProfileScope
interface UserProfileComponent {
    fun inject(userProfileComponent: UserProfileFragment)
}
