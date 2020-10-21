package ru.krivonosovdenis.fintechapp.rvcomponents

import android.graphics.Canvas
import android.util.Log
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
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.PostRenderData
import ru.krivonosovdenis.fintechapp.utils.humanizePostDate

class PostsFeedAdapter :
    RecyclerView.Adapter<PostsFeedAdapter.BaseViewHolder>(), ItemTouchHelperAdapter,
    CustomItemDecorationAdapter {
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

    abstract class BaseViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    class PostWithoutPhotoViewHolder(override val containerView: View) :
        BaseViewHolder(containerView)

    class PostWithPhotoViewHolder(override val containerView: View) : BaseViewHolder(containerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEWHOLDER_WITHOUT_PHOTO -> PostWithoutPhotoViewHolder(
                inflater.inflate(
                    R.layout.post_without_photo_wraper,
                    parent,
                    false
                )
            )
            VIEWHOLDER_WITH_PHOTO -> PostWithPhotoViewHolder(
                inflater.inflate(
                    R.layout.post_with_photo_wraper,
                    parent,
                    false
                )
            )
            else -> PostWithPhotoViewHolder(
                inflater.inflate(
                    R.layout.soc_network_post_with_photo,
                    parent,
                    false
                )
            )
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
                viewHolder.containerView.postDate.text = humanizePostDate(post.date)
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
                viewHolder.containerView.postDate.text = humanizePostDate(post.date)
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
        Log.e("ogogogo", position.toString());
        val innerPosts = posts.toMutableList()
        innerPosts.removeAt(position)
        posts = innerPosts
    }

    override fun onItemLiked(position: Int) {
        val posts = posts
        posts[position].isLiked = true
        notifyItemChanged(position)
    }

    override fun drawCustomDivider(
        position: Int,
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        //Скорее всего, реализация кастомного дивайдера должна быть здесь, но я не разобрался
        //с ним.
    }
}
