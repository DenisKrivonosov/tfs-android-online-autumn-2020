package ru.krivonosovdenis.fintechapp.rvcomponents

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileData

class DiffCallback : DiffUtil.ItemCallback<InfoRepresentationClass>() {
    override fun areItemsTheSame(oldItem: InfoRepresentationClass, newItem: InfoRepresentationClass): Boolean {
        return when(oldItem){
            is UserProfileData->{
                when(newItem){
                    is UserProfileData -> oldItem.userId == newItem.userId
                    is PostData -> false
                    is CommentData -> false
                }
            }
            is PostData ->{
                when(newItem){
                    is UserProfileData -> false
                    is PostData -> oldItem.postId == newItem.postId && oldItem.sourceId == newItem.sourceId
                    is CommentData -> false
                }
            }

            is CommentData ->{
                when(newItem){
                    is UserProfileData -> false
                    is PostData -> false
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
        if(oldItem is PostData && newItem is PostData){
            if (oldItem.isLiked != newItem.isLiked){
                diffBundle.putBoolean("NEW_IS_LIKED",newItem.isLiked)
                diffBundle.putInt("NEW_LIKES_COUNT",newItem.likesCount)
            }
        }
        return diffBundle
    }
}
