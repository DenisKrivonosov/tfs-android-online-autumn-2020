package ru.krivonosovdenis.fintechapp.rvcomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.post_comment.view.*
import kotlinx.android.synthetic.main.post_details.*
import kotlinx.android.synthetic.main.post_details.view.*
import kotlinx.android.synthetic.main.post_details.view.postActionLike
import kotlinx.android.synthetic.main.post_details.view.postDate
import kotlinx.android.synthetic.main.post_details.view.postLikesCounter
import kotlinx.android.synthetic.main.post_details.view.postText
import kotlinx.android.synthetic.main.post_details.view.posterAvatar
import kotlinx.android.synthetic.main.post_details.view.posterName
import kotlinx.android.synthetic.main.post_without_photo.view.*
import kotlinx.android.synthetic.main.user_profile_info_layout.view.*
import org.joda.time.LocalDate
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo
import ru.krivonosovdenis.fintechapp.interfaces.PostDetailsActions
import ru.krivonosovdenis.fintechapp.utils.humanizeDate


class PostDetailsRVAdapter(
    private val callbackInterface: PostDetailsActions,
) :
    RecyclerView.Adapter<PostDetailsRVAdapter.BaseViewHolder>() {

    companion object {
        private const val VIEW_HOLDER_POST_DETAILS = 1
        private const val VIEW_HOLDER_COMMENT = 2
    }

    private val differ = AsyncListDiffer(this, DiffCallback())
    var dataUnits: MutableList<InfoRepresentationClass>
        set(value) {
            differ.submitList(value)
        }
        get() = differ.currentList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = when (viewType) {
            VIEW_HOLDER_POST_DETAILS ->
                inflater.inflate(
                    R.layout.post_details_wrapper,
                    parent,
                    false
                )
            VIEW_HOLDER_COMMENT ->
                inflater.inflate(
                    R.layout.post_comment,
                    parent,
                    false
                )

            else -> throw IllegalArgumentException("это какой-то неправильный тип данных")
        }
        return when (viewType) {
            VIEW_HOLDER_POST_DETAILS -> PostDetailsViewHolder(view)
            VIEW_HOLDER_COMMENT -> CommentViewHolder(view)
            else -> throw IllegalArgumentException("это какой-то неправильный тип данных")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bindHolder(holder, position)
    }

    private fun bindHolder(viewHolder: BaseViewHolder, position: Int) {
        val context = viewHolder.itemView.context
        val currentDate = LocalDate().toDateTimeAtCurrentTime().millis
        when (viewHolder.itemViewType) {
            VIEW_HOLDER_POST_DETAILS -> {
                val post = dataUnits[position] as PostFullData
                Glide.with(context)
                    .load(post.posterAvatar)
                    .centerCrop()
                    .into(viewHolder.containerView.posterAvatar)
                viewHolder.containerView.posterName.text = post.posterName
                viewHolder.containerView.postDate.text =
                    humanizeDate(LocalDate().toDateTimeAtCurrentTime().millis, post.date.millis)
                viewHolder.containerView.postText.text = post.text
                if (post.photo != null) {
                    Glide.with(context)
                        .load(post.photo)
                        .into(viewHolder.containerView.postImage)
                } else {
                    viewHolder.containerView.postImage.isGone = true
                    viewHolder.containerView.postActionShare.isGone = true
                }
                viewHolder.containerView.postActionLike.background = if (post.isLiked) {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.post_like_button_clicked_icon,
                        context.theme
                    )
                } else {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.post_like_button_icon,
                        context.theme
                    )
                }
                viewHolder.containerView.postLikesCounter.text = post.likesCount.toString()

                if (!post.photo.isNullOrEmpty()) {
                    viewHolder.containerView.postActionShare.isVisible = true
                    viewHolder.containerView.postActionShare.setOnClickListener {
                        callbackInterface.sharePostImage(post)
                    }
                    viewHolder.containerView.postActionSave.isVisible = true
                    viewHolder.containerView.postActionSave.setOnClickListener {
                        callbackInterface.savePostImage(post)
                    }
                } else {
                    viewHolder.containerView.postActionShare.isVisible = false
                    viewHolder.containerView.postActionSave.isVisible = false
                }
            }
            VIEW_HOLDER_COMMENT -> {
                val comment = dataUnits[position] as CommentData
                Glide.with(context)
                    .load(comment.commenterAvatar)
                    .centerCrop()
                    .into(viewHolder.containerView.commenterAvatar)
                viewHolder.containerView.commenterName.text = comment.commenterName
                viewHolder.containerView.commentText.text = comment.text
                viewHolder.containerView.commentDate.text = humanizeDate(currentDate, comment.date.millis)
                viewHolder.containerView.commentLikesCount.text = comment.likesCount.toString()
                viewHolder.containerView.likeIcon.background =
                    if (comment.isLiked) {
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.post_like_button_clicked_icon,
                            context.theme
                        )
                    } else {
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.post_like_button_icon,
                            context.theme
                        )
                    }
            }

        }
    }

    override fun getItemCount(): Int {
        return dataUnits.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (val data = dataUnits[position]) {
            is UserProfileMainInfo -> throw java.lang.IllegalArgumentException("адаптер не поддерживает данные этого типа")
            is PostFullData -> VIEW_HOLDER_POST_DETAILS
            is CommentData -> VIEW_HOLDER_COMMENT
        }
    }



    abstract class BaseViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    class PostDetailsViewHolder(override val containerView: View) :
        BaseViewHolder(containerView)

    class CommentViewHolder(override val containerView: View) : BaseViewHolder(containerView)


}
