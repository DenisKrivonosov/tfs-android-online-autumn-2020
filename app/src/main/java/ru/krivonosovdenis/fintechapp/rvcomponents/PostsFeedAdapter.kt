package ru.krivonosovdenis.fintechapp.rvcomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.soc_network_post_with_photo.view.*
import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.postActionLike
import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.postDate
import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.postText
import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.posterAvatar
import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.posterName
import org.joda.time.format.DateTimeFormat
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.PostRenderData
import ru.krivonosovdenis.fintechapp.utils.humanizePostDate

class PostsFeedAdapter :
    RecyclerView.Adapter<PostsFeedAdapter.BaseViewHolder>(), ItemTouchHelperAdapter,
    DecorationTypeProvider {

    companion object {
        private const val VIEWHOLDER_WITHOUT_PHOTO = 1
        private const val VIEWHOLDER_WITH_PHOTO = 2
    }

    private val differ = AsyncListDiffer(this, DiffCallback())

    var posts: MutableList<PostRenderData>
        set(value) {
            differ.submitList(value)
        }
        get() = differ.currentList

    private val dateFormatWithYear = DateTimeFormat.forPattern("dd MMMM YYYY")
    private val dateFormatWithoutYear = DateTimeFormat.forPattern("dd MMMM")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEWHOLDER_WITHOUT_PHOTO -> PostWithoutPhotoViewHolder(
                inflater.inflate(
                    R.layout.post_without_photo_wrapper,
                    parent,
                    false
                )
            )
            VIEWHOLDER_WITH_PHOTO -> PostWithPhotoViewHolder(
                inflater.inflate(
                    R.layout.post_with_photo_wrapper,
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("это какой-то неправильный тип поста")
        }
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        val context = viewHolder.itemView.context
        val post = posts[position]
        when (viewHolder.itemViewType) {
            VIEWHOLDER_WITHOUT_PHOTO -> {
                Glide.with(context)
                    .load(post.groupAvatar)
                    .centerCrop()
                    .into(viewHolder.containerView.posterAvatar)
                viewHolder.containerView.posterName.text = post.groupName
                viewHolder.containerView.postDate.text = humanizePostDate(post.date.millis)
                viewHolder.containerView.postText.text = post.text
                viewHolder.containerView.postActionLike.background =
                    if (post.isLiked) {
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
            VIEWHOLDER_WITH_PHOTO -> {
                Glide.with(context)
                    .load(post.groupAvatar)
                    .centerCrop()
                    .into(viewHolder.containerView.posterAvatar)
                viewHolder.containerView.posterName.text = post.groupName
                viewHolder.containerView.postDate.text = humanizePostDate(post.date.millis)
                viewHolder.containerView.postText.text = post.text
                Glide.with(context)
                    .load(post.photo)
                    .into(viewHolder.containerView.postImage)
                viewHolder.containerView.postActionLike.background =
                    if (post.isLiked) {
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
        return posts.size
    }

    override fun getItemViewType(position: Int) =
        if (posts[position].photo != null) VIEWHOLDER_WITH_PHOTO else VIEWHOLDER_WITHOUT_PHOTO

    override fun onItemDismiss(position: Int) {
        val innerPosts = posts.toMutableList()
        innerPosts.removeAt(position)
        posts = innerPosts
    }

    override fun onItemLiked(position: Int) {
        posts[position].isLiked = true
        notifyItemChanged(position)
    }

    override fun getDecorationType(position: Int): PostsFeedDecorationType {
        if (position == RecyclerView.NO_POSITION) {
            return PostsFeedDecorationType.Space
        }
        if (posts.isEmpty()) {
            return PostsFeedDecorationType.Space
        }
        if (position == 0) {
            return PostsFeedDecorationType.WithText(posts[0].date.toString(dateFormatWithoutYear))
        }
        val current = posts[position]
        val previous = posts[position - 1]
        return when {
            current.date.dayOfYear().get() == previous.date.dayOfYear().get()
                    && current.date.year().get() == previous.date.year()
                .get() -> PostsFeedDecorationType.Space
            current.date.year().get() != previous.date.year()
                .get() -> PostsFeedDecorationType.WithText(
                posts[position].date.toString(
                    dateFormatWithYear
                )
            )
            else -> PostsFeedDecorationType.WithText(
                posts[position].date.toString(
                    dateFormatWithoutYear
                )
            )
        }
    }

    abstract class BaseViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    class PostWithoutPhotoViewHolder(override val containerView: View) :
        BaseViewHolder(containerView)

    class PostWithPhotoViewHolder(override val containerView: View) : BaseViewHolder(containerView)

}
