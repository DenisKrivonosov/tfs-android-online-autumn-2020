package ru.krivonosovdenis.fintechapp.presentation.userprofile

interface UserProfileView {

    fun setRefreshing(isRefreshing: Boolean)

    fun scrollViewToTop()

    fun showProfileInfo()

    fun showLoadingView()

    fun showErrorView()

}