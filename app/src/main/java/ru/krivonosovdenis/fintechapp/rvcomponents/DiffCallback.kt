package ru.krivonosovdenis.fintechapp.rvcomponents

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo

class DiffCallback : DiffUtil.ItemCallback<InfoRepresentationClass>() {
    override fun areItemsTheSame(oldItem: InfoRepresentationClass, newItem: InfoRepresentationClass): Boolean {
        return when(oldItem){
            is UserProfileMainInfo->{
                when(newItem){
                    is UserProfileMainInfo -> oldItem.userId == newItem.userId
                    is PostFullData -> false
                    is CommentData -> false
                }
            }
            is PostFullData ->{
                when(newItem){
                    is UserProfileMainInfo -> false
                    is PostFullData -> oldItem.postId == newItem.postId && oldItem.sourceId == newItem.sourceId
                    is CommentData -> false
                }
            }

            is CommentData ->{
                when(newItem){
                    is UserProfileMainInfo -> false
                    is PostFullData -> false
                    is CommentData -> oldItem.commentId == newItem.commentId && oldItem.ownerId == newItem.ownerId
                }
            }
        }


    }

    override fun areContentsTheSame(oldItem: InfoRepresentationClass, newItem: InfoRepresentationClass): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: InfoRepresentationClass, newItem: InfoRepresentationClass): Any {
        val diffBundle = Bundle()
        if(oldItem is PostFullData && newItem is PostFullData){
            if (oldItem.isLiked != newItem.isLiked){
                diffBundle.putBoolean("NEW_IS_LIKED",newItem.isLiked)
                diffBundle.putInt("NEW_LIKES_COUNT",newItem.likesCount)
            }
        }
        return diffBundle
    }
}
