package ru.krivonosovdenis.fintechapp.di

import dagger.Component
import ru.krivonosovdenis.fintechapp.presentation.postsfeed.PostsFeedFragment

@Component(dependencies = [AppComponent::class],modules = [PostsFeedModule::class])
@PostsFeedScope
interface PostsFeedComponent {
    fun inject(postsFeedFragment:PostsFeedFragment)
}