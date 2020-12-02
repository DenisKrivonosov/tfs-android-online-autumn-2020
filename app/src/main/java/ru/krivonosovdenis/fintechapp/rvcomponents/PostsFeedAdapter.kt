//package ru.krivonosovdenis.fintechapp.rvcomponents
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.res.ResourcesCompat
//import androidx.recyclerview.widget.AsyncListDiffer
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import kotlinx.android.extensions.LayoutContainer
//import kotlinx.android.synthetic.main.soc_network_post_with_photo.view.*
//import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.postActionLike
//import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.postDate
//import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.postText
//import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.posterAvatar
//import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.posterName
//import org.joda.time.LocalDate
//import org.joda.time.format.DateTimeFormat
//import ru.krivonosovdenis.fintechapp.R
//import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
//import ru.krivonosovdenis.fintechapp.interfaces.AllPostsActions
//import ru.krivonosovdenis.fintechapp.utils.humanizePostDate
//
//class PostsFeedAdapter(private val callbackInterface: AllPostsActions) :
//    RecyclerView.Adapter<PostsFeedAdapter.BaseViewHolder>(), ItemTouchHelperAdapter,
//    DecorationTypeProvider {
//
//    companion object {
//        private const val VIEW_HOLDER_WITHOUT_PHOTO = 1
//        private const val VIEW_HOLDER_WITH_PHOTO = 2
//    }
//
//    private val differ = AsyncListDiffer(this, DiffCallback())
//    var posts: MutableList<PostFullData>
//        set(value) {
//            differ.submitList(value)
//        }
//        get() = differ.currentList
//
//    private val dateFormatWithYear = DateTimeFormat.forPattern("dd MMMM YYYY")
//    private val dateFormatWithoutYear = DateTimeFormat.forPattern("dd MMMM")
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val view = when (viewType) {
//            VIEW_HOLDER_WITHOUT_PHOTO ->
//                inflater.inflate(
//                    R.layout.post_without_photo_wrapper,
//                    parent,
//                    false
//                )
//            VIEW_HOLDER_WITH_PHOTO ->
//                inflater.inflate(
//                    R.layout.post_with_photo_wrapper,
//                    parent,
//                    false
//                )
//            else -> throw IllegalArgumentException("это какой-то неправильный тип поста")
//        }
//        val viewHolder = when (viewType) {
//            VIEW_HOLDER_WITHOUT_PHOTO -> PostWithoutPhotoViewHolder(view)
//            VIEW_HOLDER_WITH_PHOTO -> PostWithPhotoViewHolder(view)
//            else -> throw IllegalArgumentException("это какой-то неправильный тип поста")
//        }
//        view.setOnClickListener {
//            callbackInterface.onPostClicked(posts[viewHolder.adapterPosition])
//        }
//        return viewHolder
//    }
//
//    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
//        bindHolder(holder, position)
//    }
//
//    private fun bindHolder(viewHolder: BaseViewHolder, position: Int) {
//        val context = viewHolder.itemView.context
//        val post = posts[position]
//        val currentDate = LocalDate().toDateTimeAtCurrentTime().millis
//        when (viewHolder.itemViewType) {
//            VIEW_HOLDER_WITHOUT_PHOTO -> {
//                Glide.with(context)
//                    .load(post.posterAvatar)
//                    .centerCrop()
//                    .into(viewHolder.containerView.posterAvatar)
//                viewHolder.containerView.posterName.text = post.posterName
//                viewHolder.containerView.postDate.text =
//                    humanizePostDate(currentDate, post.date.millis)
//                viewHolder.containerView.postText.text = post.text
//                viewHolder.containerView.postActionLike.background =
//                    if (post.isLiked) {
//                        ResourcesCompat.getDrawable(
//                            context.resources,
//                            R.drawable.post_like_button_clicked_icon,
//                            context.theme
//                        )
//                    } else {
//                        ResourcesCompat.getDrawable(
//                            context.resources,
//                            R.drawable.post_like_button_icon,
//                            context.theme
//                        )
//                    }
//            }
//            VIEW_HOLDER_WITH_PHOTO -> {
//                Glide.with(context)
//                    .load(post.posterAvatar)
//                    .centerCrop()
//                    .into(viewHolder.containerView.posterAvatar)
//                viewHolder.containerView.posterName.text = post.posterName
//                viewHolder.containerView.postDate.text =
//                    humanizePostDate(currentDate, post.date.millis)
//                viewHolder.containerView.postText.text = post.text
//                Glide.with(context)
//                    .load(post.photo)
//                    .into(viewHolder.containerView.postImage)
//                viewHolder.containerView.postActionLike.background =
//                    if (post.isLiked) {
//                        ResourcesCompat.getDrawable(
//                            context.resources,
//                            R.drawable.post_like_button_clicked_icon,
//                            context.theme
//                        )
//                    } else {
//                        ResourcesCompat.getDrawable(
//                            context.resources,
//                            R.drawable.post_like_button_icon,
//                            context.theme
//                        )
//                    }
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return posts.size
//    }
//
//    override fun getItemViewType(position: Int) =
//        if (posts[position].photo != null) VIEW_HOLDER_WITH_PHOTO else VIEW_HOLDER_WITHOUT_PHOTO
//
//    override fun onItemDismiss(position: Int) {
//        callbackInterface.onPostDismiss(posts[position])
//        val innerPosts = posts.toMutableList()
//        innerPosts.removeAt(position)
//        posts = innerPosts
//    }
//
//    override fun onItemLiked(position: Int) {
//        callbackInterface.onPostLiked(posts[position])
//        posts[position].isLiked = true
//        notifyItemChanged(position)
//    }
//
//    override fun getDecorationType(position: Int): PostsListDecorationType {
//        if (position == RecyclerView.NO_POSITION) {
//            return PostsListDecorationType.Space
//        }
//        if (posts.isEmpty()) {
//            return PostsListDecorationType.Space
//        }
//        if (position == 0) {
//            return PostsListDecorationType.WithText(posts[0].date.toString(dateFormatWithoutYear))
//        }
//        val current = posts[position]
//        val previous = posts[position - 1]
//        return when {
//            current.date.dayOfYear().get() == previous.date.dayOfYear().get()
//                    && current.date.year().get() == previous.date.year()
//                .get() -> PostsListDecorationType.Space
//            current.date.year().get() != previous.date.year()
//                .get() -> PostsListDecorationType.WithText(
//                posts[position].date.toString(
//                    dateFormatWithYear
//                )
//            )
//            else -> PostsListDecorationType.WithText(
//                posts[position].date.toString(
//                    dateFormatWithoutYear
//                )
//            )
//        }
//
//    }
//
//    abstract class BaseViewHolder(override val containerView: View) :
//        RecyclerView.ViewHolder(containerView), LayoutContainer
//
//    class PostWithoutPhotoViewHolder(override val containerView: View) :
//        BaseViewHolder(containerView)
//
//    class PostWithPhotoViewHolder(override val containerView: View) : BaseViewHolder(containerView)
//
//}
