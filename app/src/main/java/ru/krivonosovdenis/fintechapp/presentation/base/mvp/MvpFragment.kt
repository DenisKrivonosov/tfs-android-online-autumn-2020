package ru.krivonosovdenis.fintechapp.presentation.base.mvp

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.presenter.Presenter

abstract class MvpFragment<View, P : Presenter<View>> : Fragment(),
    MvpViewCallback<View, P> {

    private val mvpHelper: MvpHelper<View, P> by lazy { MvpHelper(this) }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mvpHelper.create()
    }

    override fun onDestroyView() {
        val isFinishing = isRemoving || requireActivity().isFinishing
        mvpHelper.destroy(isFinishing)
        super.onDestroyView()
    }
}
