package ru.krivonosovdenis.fintechapp.presentation.postsfeed

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_all_posts.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.krivonosovdenis.fintechapp.ApplicationClass
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.interfaces.CommonAdapterActions
import ru.krivonosovdenis.fintechapp.presentation.mainactivity.MainActivity
import ru.krivonosovdenis.fintechapp.rvcomponents.CommonRVAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.ItemTouchHelperAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.ItemTouchHelperCallback
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsListItemDecoration
import ru.krivonosovdenis.fintechapp.utils.postDelayedSafe
import javax.inject.Inject

class PostsFeedFragment : MvpAppCompatFragment(), PostsFeedView,
    CommonAdapterActions, SwipeRefreshLayout.OnRefreshListener {


//
//    @Inject
//    lateinit var presenter1: PostsFeedPresenter

//    @InjectPresenter
//    lateinit var presenter1:PostsFeedPresenter

//    lateinit var presenter1: PostsFeedPresenter
//
//    @ProvidePresenter
//    fun getPresenter(): PostsFeedPresenter = presenter1


//    @Inject
//    lateinit var presenterProvider: Provider<PostsFeedPresenter>
//    private val presenter by  { presenterProvider.get() }


    private lateinit var rvAdapter: CommonRVAdapter


    @Inject
    @InjectPresenter
    lateinit var presenter: PostsFeedPresenter

    @ProvidePresenter
    fun provide() = presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.applicationContext as ApplicationClass).addPostsFeedComponent()
        (activity?.applicationContext as ApplicationClass).postsFeedComponent?.inject(this)
        (activity as MainActivity).showBottomSettings()
    }

    override fun onDetach() {
        (activity?.applicationContext as ApplicationClass).clearPostsFeedComponent()
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_posts, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allPostsSwipeRefreshLayout.setOnRefreshListener(this)
        (activity as MainActivity).showBottomNavigationTabs()
        rvAdapter = CommonRVAdapter(this)
        with(allPostsView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
            addItemDecoration(PostsListItemDecoration())
        }
        val callback = ItemTouchHelperCallback(rvAdapter as ItemTouchHelperAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(allPostsView)
        getFeedPostsFromApi()
        presenter.subscribeOnAllPostsFromDB()
    }

    override fun onRefresh() {
        getFeedPostsFromApi()
    }

    override fun setRefreshing(isRefreshing: Boolean) {
        allPostsSwipeRefreshLayout.isRefreshing = isRefreshing
    }

    override fun showPosts(posts: List<PostData>) {
        Log.e("POSTSSIZE", posts.count().toString());
        val oldPosts = rvAdapter.dataUnits
        allPostsView.isVisible = true
        loadingView.isGone = true
        errorView.isGone = true
        rvAdapter.dataUnits = posts.toMutableList()
        //Смотрим появились ли новые посты. Если появились - скролим скроллвью наверх
        posts.forEach { newPost ->
            if (oldPosts.find {
                    ((it as PostData).sourceId == newPost.sourceId)
                            && ((it).postId == newPost.postId)
                } == null) {
                allPostsView.postDelayedSafe(300) {
                    allPostsView.smoothScrollToPosition(
                        0,
                    )
                }
                return@forEach
            }
        }
    }

    override fun showLoadingView() {
        allPostsView.isGone = true
        loadingView.isVisible = true
        errorView.isGone = true
    }

    override fun showPostsView() {
        allPostsView.isVisible = true
        loadingView.isGone = true
        errorView.isGone = true
    }

    override fun showDbGetFeedErrorView() {
        allPostsView.isGone = true
        loadingView.isGone = true
        errorView.isVisible = true
    }

    override fun showPostUpdateErrorToast() {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.post_update_error_text),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPostDismiss(post: PostData) {
        Log.e("heredeletingpost", post.text)
        deletePostFromFeedOnApi(post)
    }

    override fun onPostLiked(post: PostData) {
        likePostOnApi(post)
    }

    override fun onPostDisliked(post: PostData) {
        dislikePostOnApi(post)
    }

    override fun onPostClicked(post: PostData) {
        (activity as MainActivity).openPostDetails(post)
    }

    private fun getFeedPostsFromApi(){
        if(!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable){
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_show_cached))
            allPostsSwipeRefreshLayout.isRefreshing = false
        }
        else{
            presenter.loadPostsFromApiAndInsertIntoDB()
        }
    }

    private fun likePostOnApi(post:PostData){
        if(!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable){
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_can_not_perform_action))
        }
        else{
            presenter.likePostOnApiAndDb(post)
        }
    }

    private fun dislikePostOnApi(post:PostData){
        if(!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable){
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_can_not_perform_action))
        }
        else{
            presenter.dislikePostOnApiAndDb(post)
        }
    }

    private fun deletePostFromFeedOnApi(post:PostData){
        if(!(requireActivity().application as ApplicationClass).isNetworkAvailableVariable){
            networkIsNotAvailableMessage(resources.getString(R.string.network_is_not_available_can_not_perform_action))
        }
        else{
            presenter.deletePostOnApiAndDb(post)
        }
    }

    private fun networkIsNotAvailableMessage(toastText:String){
        Toast.makeText(
            requireContext(),
            toastText,
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        fun newInstance(): PostsFeedFragment {
            return PostsFeedFragment()
        }
    }

}
