package ru.krivonosovdenis.fintechapp.rvcomponents

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import ru.krivonosovdenis.fintechapp.dataclasses.InfoRepresentationClass
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileMainInfo

class DiffCallback : DiffUtil.ItemCallback<InfoRepresentationClass>() {
    override fun areItemsTheSame(oldItem: InfoRepresentationClass, newItem: InfoRepresentationClass): Boolean {
        return if(oldItem is UserProfileMainInfo && newItem is UserProfileMainInfo){
            oldItem.userId == newItem.userId
        } else if(oldItem is PostFullData &&  newItem is PostFullData) {
            oldItem.postId == newItem.postId && oldItem.sourceId == newItem.sourceId
        } else {
            throw IllegalArgumentException()
        }

    }

    override fun areContentsTheSame(oldItem: InfoRepresentationClass, newItem: InfoRepresentationClass): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: InfoRepresentationClass, newItem: InfoRepresentationClass): Any? {
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
