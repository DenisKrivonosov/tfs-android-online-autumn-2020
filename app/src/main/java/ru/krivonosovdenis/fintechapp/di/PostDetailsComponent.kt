package ru.krivonosovdenis.fintechapp.di

import dagger.Component
import ru.krivonosovdenis.fintechapp.ui.postdetails.PostDetailsFragment

@Component(dependencies = [AppComponent::class], modules = [PostDetailsModule::class])
@PostDetailsScope
interface PostDetailsComponent {
    fun inject(postDetailsFragment: PostDetailsFragment)
}
