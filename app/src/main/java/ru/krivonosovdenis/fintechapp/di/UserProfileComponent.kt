package ru.krivonosovdenis.fintechapp.di

import dagger.Component
import ru.krivonosovdenis.fintechapp.presentation.likedposts.LikedPostsFragment
import ru.krivonosovdenis.fintechapp.presentation.sendpost.SendPostFragment
import ru.krivonosovdenis.fintechapp.presentation.userprofile.UserProfileFragment

@Component(dependencies = [AppComponent::class],modules = [UserProfileModule::class])
@UserProfileScope
interface UserProfileComponent {
    fun inject(userProfileComponent: UserProfileFragment)
}