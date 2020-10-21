package ru.krivonosovdenis.fintechapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.soc_network_post_details.*
import ru.krivonosovdenis.fintechapp.MainActivity
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.PostRenderData
import ru.krivonosovdenis.fintechapp.utils.humanizePostDate

class PostDetailsFragment : Fragment() {

    companion object {
        private const val POST_DATA = "post_data"

        fun newInstance(postData: PostRenderData): PostDetailsFragment {
            val arguments = Bundle()
            arguments.putParcelable(POST_DATA, postData)
            val fragment = PostDetailsFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).postsBottomNavigation.visibility = GONE
        val post = arguments!!.getParcelable<PostRenderData>(POST_DATA) as PostRenderData
        Glide.with(activity as MainActivity)
            .load(post.groupAvatar)
            .centerCrop()
            .into(posterAvatar)
        posterName.text = post.groupName
        postDate.text = humanizePostDate(post.date.millis)
        postText.text = post.text
        if (post.photo != null) {
            Glide.with(activity as MainActivity)
                .load(post.photo)
                .into(postImage)
        } else {
            postImage.visibility = GONE
        }
        postActionLike.background = if (post.isLiked) {
            ResourcesCompat.getDrawable(
                context!!.resources,
                R.drawable.post_like_button_clicked_icon,
                context!!.theme
            )
        } else {
            ResourcesCompat.getDrawable(
                context!!.resources,
                R.drawable.post_like_button_icon,
                context!!.theme
            )
        }
        postLikesCounter.text = post.likesCount.toString()
    }
}
