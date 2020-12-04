package ru.krivonosovdenis.fintechapp.presentation.userprofile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_user_profile.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.krivonosovdenis.fintechapp.ApplicationClass
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileData
import ru.krivonosovdenis.fintechapp.interfaces.CommonAdapterActions
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import ru.krivonosovdenis.fintechapp.rvcomponents.CommonRVAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsListItemDecoration
import ru.krivonosovdenis.fintechapp.utils.postDelayedSafe
import javax.inject.Inject

class UserProfileFragment : MvpAppCompatFragment(), UserProfileView,
    CommonAdapterActions, SwipeRefreshLayout.OnRefreshListener {
    @Inject
    @InjectPresenter
    lateinit var presenter: UserProfilePresenter

    @ProvidePresenter
    fun provide() = presenter

    private lateinit var rvAdapter: CommonRVAdapter
    private var vkUserId = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.applicationContext as ApplicationClass).addUserProfileComponent()
        (activity?.applicationContext as ApplicationClass).userProfileComponent?.inject(this)
        (activity as MainActivity).hideBottomSettings()
    }

    override fun onDetach() {
        (activity?.applicationContext as ApplicationClass).clearUserProfileComponent()
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
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
        newPostFab.setOnClickListener {
            (activity as MainActivity).showNewPostFragment(vkUserId)
        }
        loadUserProfileAndPosts()
        presenter.subscribeOnUserInfoFromDB()
        presenter.subscribeOnUserOwnPostsFromDB()
    }

    override fun onRefresh() {
        loadUserProfileAndPosts()
    }

    override fun setRefreshing(isRefreshing: Boolean) {
        userProfileSwipeRefreshLayout.isRefreshing = isRefreshing
    }

    override fun scrollViewToTop() {
        userProfileView.postDelayedSafe(100) {
            userProfileView.smoothScrollToPosition(
                0,
            )
        }
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

    override fun renderProfileAndShow(userInfoData: UserProfileData) {
        vkUserId = userInfoData.userId
        val finalData = mutableListOf<InfoRepresentationClass>()
        finalData.add(userInfoData)
        val postsData = rvAdapter.dataUnits.filterIsInstance<PostData>()
        finalData.addAll(postsData)
        rvAdapter.dataUnits = finalData
        userProfileView.postDelayedSafe(100) {
            userProfileView.smoothScrollToPosition(
                0,
            )
        }
        userProfileView.isVisible = true
        loadingView.isGone = true
        dbLoadingErrorView.isGone = true
        newPostFab.isVisible = true
    }

    override fun renderUserPostsAndShow(posts: List<PostData>) {
        val finalData = mutableListOf<InfoRepresentationClass>()
        val userInfoData = rvAdapter.dataUnits.find { it is UserProfileData }
        if (userInfoData != null) {
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
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.post_update_error_text),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPostDismiss(post: PostData) {
        //В общем этот метод здесь не реализован. Вышла накладочка с архитектурой и солидом в частности
    }

    override fun onPostLiked(post: PostData) {
        likePostOnApi(post)
    }

    override fun onPostDisliked(post: PostData) {
        dislikePostOnApi(post)
    }

    private fun likePostOnApi(post: PostData) {
        if (!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable) {
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_can_not_perform_action))
        } else {
            presenter.likePostOnApiAndDb(post)
        }
    }

    private fun dislikePostOnApi(post: PostData) {
        if (!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable) {
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_can_not_perform_action))
        } else {
            presenter.dislikePostOnApiAndDb(post)
        }
    }

    private fun loadUserProfileAndPosts() {
        if (!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable) {
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_show_cached))
            userProfileSwipeRefreshLayout.isRefreshing = false
        } else {
            presenter.loadUserProfileAndPostsInfoFromApiAndInsertIntoDB()
        }
    }

    private fun networkIsNotAvailableMessage(toastText: String) {
        Toast.makeText(
            requireContext(),
            toastText,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPostClicked(post: PostData) {
        (activity as MainActivity).openPostDetails(post)
    }

    companion object {
        fun newInstance(): UserProfileFragment {
            return UserProfileFragment()
        }
    }
}
