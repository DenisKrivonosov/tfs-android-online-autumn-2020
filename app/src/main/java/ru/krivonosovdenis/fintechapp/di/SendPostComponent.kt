package ru.krivonosovdenis.fintechapp.di

import dagger.Component
import ru.krivonosovdenis.fintechapp.ui.sendpost.SendPostFragment

@Component(dependencies = [AppComponent::class], modules = [SendPostModule::class])
@SendPostScope
interface SendPostComponent {
    fun inject(sendPostFragment: SendPostFragment)
}
