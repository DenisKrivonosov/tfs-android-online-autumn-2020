package ru.krivonosovdenis.fintechapp.presentation.mainactivity

interface MainActivityView {

    fun openPostDetails(post: PostFullData)

    fun showBottomNavigationTabs()

    fun setLikedPostsVisibility(visibilityFlag: Boolean)


}
