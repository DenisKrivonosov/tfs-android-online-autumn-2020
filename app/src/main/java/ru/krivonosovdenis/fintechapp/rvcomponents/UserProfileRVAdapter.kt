package ru.krivonosovdenis.fintechapp.rvcomponents

import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo
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
import kotlinx.android.synthetic.main.soc_network_post_with_photo.view.*
import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.postActionLike
import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.postDate
import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.postText
import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.posterAvatar
import kotlinx.android.synthetic.main.soc_network_post_without_photo.view.posterName
import kotlinx.android.synthetic.main.user_profile_info_layout.view.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.interfaces.AllPostsActions
import ru.krivonosovdenis.fintechapp.utils.humanizePostDate

class UserProfileRVAdapter(private val callbackInterface: AllPostsActions) :
    RecyclerView.Adapter<UserProfileRVAdapter.BaseViewHolder>(), ItemTouchHelperAdapter,
    DecorationTypeProvider {

    companion object {
        private const val VIEW_HOLDER_POST_WITHOUT_PHOTO = 1
        private const val VIEW_HOLDER_POST_WITH_PHOTO = 2
        private const val VIEW_HOLDER_USER_INFO = 3
    }

    private val differ = AsyncListDiffer(this, DiffCallback())
    var dataUnits: MutableList<InfoRepresentationClass>
        set(value) {
            differ.submitList(value)
        }
        get() = differ.currentList

    private val dateFormatWithYear = DateTimeFormat.forPattern("dd MMMM YYYY")
    private val dateFormatWithoutYear = DateTimeFormat.forPattern("dd MMMM")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = when (viewType) {
            VIEW_HOLDER_POST_WITHOUT_PHOTO ->
                inflater.inflate(
                    R.layout.post_without_photo_wrapper,
                    parent,
                    false
                )
            VIEW_HOLDER_POST_WITH_PHOTO ->
                inflater.inflate(
                    R.layout.post_with_photo_wrapper,
                    parent,
                    false
                )
            VIEW_HOLDER_USER_INFO ->
                inflater.inflate(
                    R.layout.user_profile_info_layout,
                    parent,
                    false
                )
            else -> throw IllegalArgumentException("это какой-то неправильный тип поста")
        }
        val viewHolder = when (viewType) {
            VIEW_HOLDER_POST_WITHOUT_PHOTO -> PostWithoutPhotoViewHolder(view)
            VIEW_HOLDER_POST_WITH_PHOTO -> PostWithPhotoViewHolder(view)
            VIEW_HOLDER_USER_INFO -> UserProfileInfoViewHolder(view)
            else -> throw IllegalArgumentException("это какой-то неправильный тип данных")
        }
        view.setOnClickListener {
            if(viewType!= VIEW_HOLDER_USER_INFO) {
                callbackInterface.onPostClicked(dataUnits[viewHolder.adapterPosition] as PostFullData)

            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bindHolder(holder, position)
    }

    private fun bindHolder(viewHolder: BaseViewHolder, position: Int) {
        val context = viewHolder.itemView.context
        val currentDate = LocalDate().toDateTimeAtCurrentTime().millis
        when (viewHolder.itemViewType) {
            VIEW_HOLDER_POST_WITHOUT_PHOTO -> {
                val post = dataUnits[position] as PostFullData
                Glide.with(context)
                    .load(post.posterAvatar)
                    .centerCrop()
                    .into(viewHolder.containerView.posterAvatar)
                viewHolder.containerView.posterName.text = post.posterName
                viewHolder.containerView.postDate.text =
                    humanizePostDate(currentDate, post.date.millis)
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
            VIEW_HOLDER_POST_WITH_PHOTO -> {
                val post = dataUnits[position] as PostFullData
                Glide.with(context)
                    .load(post.posterAvatar)
                    .centerCrop()
                    .into(viewHolder.containerView.posterAvatar)
                viewHolder.containerView.posterName.text = post.posterName
                viewHolder.containerView.postDate.text =
                    humanizePostDate(currentDate, post.date.millis)
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
            VIEW_HOLDER_USER_INFO->{
                val userData = dataUnits[position] as UserProfileMainInfo
                Glide.with(context)
                    .load(userData.photo)
                    .centerCrop()
                    .into(viewHolder.containerView.userAvatar)
                viewHolder.containerView.userName.text = "${userData.firstName} ${userData.lastName}"
                viewHolder.containerView.userStatus.text = "test status"
                if(userData.city!=null){
                    viewHolder.containerView.cityLayoutGroup.isVisible = true
                    viewHolder.containerView.userCity.text = userData.city
                }
                else {
                    viewHolder.containerView.cityLayoutGroup.isGone = true
                }
                if(userData.country!=null){
                    viewHolder.containerView.countryLayoutGroup.isVisible = true
                    viewHolder.containerView.userCountry.text = userData.country
                }
                else {
                    viewHolder.containerView.countryLayoutGroup.isGone = true
                }
                if(userData.universityName!=null){
                    viewHolder.containerView.educationUniversityLayoutGroup.isVisible = true
                    viewHolder.containerView.userEducationUniversity.text = userData.universityName
                }
                else {
                    viewHolder.containerView.educationUniversityLayoutGroup.isGone = true
                }
                if(userData.facultyName!=null){
                    viewHolder.containerView.educationFacultyLayoutGroup.isVisible = true
                    viewHolder.containerView.userEducationFaculty.text = userData.facultyName
                }
                else {
                    viewHolder.containerView.educationFacultyLayoutGroup.isGone = true
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataUnits.size
    }

    override fun getItemViewType(position: Int):Int {
        return when(val data =dataUnits[position]){
            is UserProfileMainInfo -> VIEW_HOLDER_USER_INFO
            is PostFullData -> if (data.photo != null) VIEW_HOLDER_POST_WITH_PHOTO else VIEW_HOLDER_POST_WITHOUT_PHOTO
        }
//        if(dataUnits[position] is UserProfileMainInfo) {
//            return VIEW_HOLDER_USER_INFO
//        }
//        else {
//            return if (dataUnits[position].photo != null) VIEW_HOLDER_WITH_PHOTO else VIEW_HOLDER_WITHOUT_PHOTO
//
//        }

    }

    override fun onItemDismiss(position: Int) {
        callbackInterface.onPostDismiss(dataUnits[position])
        val innerPosts = dataUnits.toMutableList()
        innerPosts.removeAt(position)
        dataUnits = innerPosts
    }

    override fun onItemLiked(position: Int) {
        callbackInterface.onPostLiked(dataUnits[position])
        dataUnits[position].isLiked = true
        notifyItemChanged(position)
    }

    override fun getDecorationType(position: Int): PostsListDecorationType {
        if (position == RecyclerView.NO_POSITION) {
            return PostsListDecorationType.Space
        }
        if (dataUnits.isEmpty()) {
            return PostsListDecorationType.Space
        }
        if (position == 0) {
            return PostsListDecorationType.WithText(dataUnits[0].date.toString(dateFormatWithoutYear))
        }
        val current = dataUnits[position]
        val previous = dataUnits[position - 1]
        return when {
            current.date.dayOfYear().get() == previous.date.dayOfYear().get()
                    && current.date.year().get() == previous.date.year()
                .get() -> PostsListDecorationType.Space
            current.date.year().get() != previous.date.year()
                .get() -> PostsListDecorationType.WithText(
                posts[position].date.toString(
                    dateFormatWithYear
                )
            )
            else -> PostsListDecorationType.WithText(
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
    class UserProfileInfoViewHolder(override val containerView: View) : BaseViewHolder(containerView)

}
