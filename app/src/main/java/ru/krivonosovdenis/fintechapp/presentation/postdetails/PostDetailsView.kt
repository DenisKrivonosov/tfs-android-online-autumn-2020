package ru.krivonosovdenis.fintechapp.presentation.postdetails

import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

interface PostDetailsView {
    fun showPost(post: PostFullData)

    fun showErrorView()

    fun showPostView()
}
