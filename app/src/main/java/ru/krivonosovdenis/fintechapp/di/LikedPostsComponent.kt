package ru.krivonosovdenis.fintechapp.di

import dagger.Component
import ru.krivonosovdenis.fintechapp.presentation.likedposts.LikedPostsFragment

@Component(dependencies = [AppComponent::class],modules = [LikedPostsModule::class])
@LikedPostsScope
interface LikedPostsComponent {
    fun inject(likedPostsFragment: LikedPostsFragment)
}