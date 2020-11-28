package ru.krivonosovdenis.fintechapp.presentation.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_all_posts.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.di.GlobalDI
import ru.krivonosovdenis.fintechapp.presentation.base.mvp.MvpFragment
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsFragment
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsPresenter
import ru.krivonosovdenis.fintechapp.presentation.postdetails.PostDetailsView
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsFeedAdapter

class UserProfileFragment: MvpFragment<UserProfileView, UserProfilePresenter>(), UserProfileView
    , SwipeRefreshLayout.OnRefreshListener {

    private lateinit var rvAdapter: PostsFeedAdapter
    private val compositeDisposable = CompositeDisposable()

    override fun getPresenter() = GlobalDI.INSTANCE.userProfilePresenter

    override fun getMvpView(): UserProfileView = this

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        getPresenter().loadUserProfileAndPostsInfoFromApiAndInsertIntoDB()
    }

    override fun onRefresh() {
        getPresenter().loadUserProfileAndPostsInfoFromApiAndInsertIntoDB()
    }

    override fun setRefreshing(isRefreshing: Boolean) {
        allPostsSwipeRefreshLayout.isRefreshing = isRefreshing
    }

    override fun scrollViewToTop() {
        //Похоже эта штука не работает из-за того, что добавление постов отрабатывает позже
        //То есть оно скролится, но на первый пост из предыдущей коллекции постов
        //буду разбираться.
        allPostsView.scrollToPosition(0)
    }

    override fun showProfileInfo() {
        TODO("Not yet implemented")
    }

    override fun showLoadingView() {
        TODO("Not yet implemented")
    }

    override fun showErrorView() {
        TODO("Not yet implemented")
    }


    companion object{
        fun newInstance(): UserProfileFragment {
            return UserProfileFragment().apply {
//                arguments = Bundle().apply {
//                    putInt(PostDetailsFragment.POST_ID, postId)
//                    putInt(PostDetailsFragment.SOURCE_ID, sourceId)
//                }
            }
        }
    }
}
