package ru.krivonosovdenis.fintechapp.ui.initloading

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.ui.mainactivity.MainActivity

class InitLoadingFragment : Fragment() {

    companion object {
        fun newInstance(): InitLoadingFragment {
            return InitLoadingFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).hideBottomSettings()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_init_loading, container, false)
    }
}
