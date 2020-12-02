package ru.krivonosovdenis.fintechapp.presentation.userprofile

import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo

interface UserProfileView {

    fun setRefreshing(isRefreshing: Boolean)

    fun scrollViewToTop()

    fun showProfileInfo()

    fun showLoadingView()

    fun showErrorView()

    fun showLoadDataFromNetworkErrorView()

    fun renderProfileAndShow(userInfoData:UserProfileMainInfo)

    fun renderUserPostsAndShow(posts:List<PostFullData>)

    fun showPostUpdateErrorToast()

}