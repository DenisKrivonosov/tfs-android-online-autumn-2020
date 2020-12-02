package ru.krivonosovdenis.fintechapp.presentation.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.loadingView
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.interfaces.CommonAdapterActions
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.MvpFragment
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import ru.krivonosovdenis.fintechapp.rvcomponents.CommonRVAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsListItemDecoration

class UserProfileFragment : MvpFragment<UserProfileView, UserProfilePresenter>(), UserProfileView,
    CommonAdapterActions, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var rvAdapter: CommonRVAdapter
    private var vkUserId = 0
    private val compositeDisposable = CompositeDisposable()

    override fun getPresenter() = GlobalDI.INSTANCE.userProfilePresenter

    override fun getMvpView(): UserProfileView = this

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().window.setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userProfileSwipeRefreshLayout.setOnRefreshListener(this)
        (activity as MainActivity).showBottomNavigationTabs()
        rvAdapter = CommonRVAdapter(this)
        with(userProfileView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
            addItemDecoration(PostsListItemDecoration())
        }
        if (GlobalDI.INSTANCE.isFirstUserProfileFragmentOpen) {
            getPresenter().loadUserProfileAndPostsInfoFromApiAndInsertIntoDB()
        }

        newPostFab.setOnClickListener {
            (activity as MainActivity).showNewPostFragment(vkUserId)
        }
        getPresenter().subscribeOnUserInfoFromDB()
        getPresenter().subscribeOnUserOwnPostsFromDB()
    }

    override fun onRefresh() {
        getPresenter().loadUserProfileAndPostsInfoFromApiAndInsertIntoDB()
    }

    override fun setRefreshing(isRefreshing: Boolean) {
        userProfileSwipeRefreshLayout.isRefreshing = isRefreshing
    }

    override fun scrollViewToTop() {
        //Похоже эта штука не работает из-за того, что добавление постов отрабатывает позже
        //То есть оно скролится, но на первый пост из предыдущей коллекции постов
        //буду разбираться.
        userProfileView.scrollToPosition(0)
    }

    override fun showProfileInfo() {
        userProfileView.isVisible = true
        newPostFab.isVisible = true
        loadingView.isGone = true
        dbLoadingErrorView.isGone = true
    }

    override fun showLoadingView() {
        userProfileView.isGone = true
        loadingView.isVisible = true
        dbLoadingErrorView.isGone = true
        newPostFab.isGone = true
    }

    override fun showErrorView() {
        userProfileView.isGone = true
        loadingView.isGone = true
        dbLoadingErrorView.isVisible = true
        newPostFab.isVisible = false
    }

    override fun showLoadDataFromNetworkErrorView() {
        userProfileView.isGone = true
        loadingView.isGone = true
        dbLoadingErrorView.isVisible = true
        newPostFab.isVisible = false
    }

    override fun renderProfileAndShow(userInfoData: UserProfileMainInfo) {
        vkUserId = userInfoData.userId
        val finalData = mutableListOf<InfoRepresentationClass>()
        val adapterData = rvAdapter.dataUnits
//        adapterData.removeAll{it is UserProfileMainInfo}
        finalData.add(userInfoData)
        finalData.addAll(adapterData)
        rvAdapter.dataUnits = finalData
        userProfileView.isVisible = true
        loadingView.isGone = true
        dbLoadingErrorView.isGone = true
        newPostFab.isVisible = true
    }

    override fun renderUserPostsAndShow(posts: List<PostFullData>) {
        val finalData = mutableListOf<InfoRepresentationClass>()
        val userInfoData = rvAdapter.dataUnits.find { it is UserProfileMainInfo }
        if(userInfoData!=null){
            finalData.add(userInfoData)
        }
        finalData.addAll(posts)
        rvAdapter.dataUnits = finalData

        userProfileView.isVisible = true
        loadingView.isGone = true
        dbLoadingErrorView.isGone = true
        newPostFab.isVisible = true
    }

    override fun showPostUpdateErrorToast() {
        Toast.makeText(requireContext(), resources.getString(R.string.post_update_error_text), Toast.LENGTH_LONG).show()
    }

    override fun onPostDismiss(post: PostFullData) {
        //В общем этот метод здесь не реализован. Вышла накладочка с архитектурой и солидом в частности
    }

    override fun onPostLiked(post: PostFullData) {
        getPresenter().likePostOnApiAndDb(post)
    }

    override fun onPostDisliked(post: PostFullData) {
        getPresenter().dislikePostOnApiAndDb(post)
    }

    override fun onPostClicked(post: PostFullData) {
        (activity as MainActivity).openPostDetails(post)
    }


    companion object {
        fun newInstance(): UserProfileFragment {
            return UserProfileFragment().apply {
//                }
            }
        }
    }
}
