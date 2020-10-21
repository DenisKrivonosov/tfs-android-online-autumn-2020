package ru.krivonosovdenis.fintechapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_liked_posts.*
import ru.krivonosovdenis.fintechapp.MainActivity
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.rvcomponents.LikedPostsAdapter
import ru.krivonosovdenis.fintechapp.rvcomponents.PostsListItemDecoration

class PostsLikedFragment : Fragment() {

    private lateinit var rvAdapter: LikedPostsAdapter

    companion object {
        fun newInstance(): PostsLikedFragment {
            return PostsLikedFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_liked_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).postsBottomNavigation.isVisible = true
        rvAdapter = LikedPostsAdapter(activity as MainActivity)
        likedPostsRecyclerView.layoutManager = LinearLayoutManager(activity)
        likedPostsRecyclerView.adapter = rvAdapter
        likedPostsRecyclerView.addItemDecoration(PostsListItemDecoration())
        rvAdapter.posts = (activity as MainActivity).getLikedPostsData()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvAdapter.posts = (activity as MainActivity).getLikedPostsData()
    }
}
