package ru.krivonosovdenis.fintechapp.presentation.sendpost

import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo

interface SendPostView {

    fun showSendPostEditorInitErrorView()

    fun showSendPostEditorView(userData:UserProfileMainInfo)

    fun showSendPostSuccessView()

    fun showSendPostErrorView()



}