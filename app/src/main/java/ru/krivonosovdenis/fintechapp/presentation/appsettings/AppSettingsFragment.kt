package ru.krivonosovdenis.fintechapp.presentation.appsettings

import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.MvpFragment

class AppSettingsFragment : MvpFragment<AppSettingsView, AppSettingsPresenter>(), AppSettingsView {
    override fun getPresenter(): AppSettingsPresenter = GlobalDI.INSTANCE.appSettingsPresenter

    override fun getMvpView(): AppSettingsView = this

    companion object {
        fun newInstance(): AppSettingsFragment {
            return AppSettingsFragment().apply {
//                arguments = Bundle().apply {
//                    putInt(PostDetailsFragment.POST_ID, postId)
//                    putInt(PostDetailsFragment.SOURCE_ID, sourceId)
//                }
            }
        }
    }
}