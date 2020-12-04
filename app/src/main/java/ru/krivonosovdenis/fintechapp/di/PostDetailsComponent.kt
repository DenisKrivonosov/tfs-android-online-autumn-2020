package ru.krivonosovdenis.fintechapp.di

import dagger.Component
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsFragment

@Component(dependencies = [AppComponent::class],modules = [PostDetailsModule::class])
@PostDetailsScope
interface PostDetailsComponent {
    fun inject(postDetailsFragment:PostDetailsFragment)
}