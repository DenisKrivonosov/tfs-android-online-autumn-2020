package ru.krivonosovdenis.fintechapp.presentation.sendpost

import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.MvpFragment

class SendPostFragment : MvpFragment<SendPostView, SendPostPresenter>(), SendPostView {
    override fun getPresenter(): SendPostPresenter = GlobalDI.INSTANCE.sendPostPresenter

    override fun getMvpView(): SendPostView = this

    companion object {
        fun newInstance(): SendPostFragment {
            return SendPostFragment().apply {
//                arguments = Bundle().apply {
//                    putInt(PostDetailsFragment.POST_ID, postId)
//                    putInt(PostDetailsFragment.SOURCE_ID, sourceId)
//                }
            }
        }
    }
}