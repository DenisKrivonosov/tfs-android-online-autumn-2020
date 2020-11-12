package ru.krivonosovdenis.fintechapp.presentation.mainactivity

import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

interface MainActivityView {

    fun openPostDetails(post: PostFullData)

    fun showBottomNavigationTabs()

    fun setLikedPostsVisibility(visibilityFlag: Boolean)


}
