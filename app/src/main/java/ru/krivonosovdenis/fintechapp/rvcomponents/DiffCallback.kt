package ru.krivonosovdenis.fintechapp.rvcomponents

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData


class DiffCallback : DiffUtil.ItemCallback<PostFullData>() {
    override fun areItemsTheSame(oldItem: PostFullData, newItem: PostFullData): Boolean {
        return oldItem.postId == newItem.postId && oldItem.sourceId == newItem.sourceId
    }

    override fun areContentsTheSame(oldItem: PostFullData, newItem: PostFullData): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: PostFullData, newItem: PostFullData): Any? {
        val diffBundle = Bundle()
        if (oldItem.isLiked != newItem.isLiked){
            diffBundle.putBoolean("NEW_IS_LIKED",newItem.isLiked)
            diffBundle.putInt("NEW_LIKES_COUNT",newItem.likesCount)
        }
        return diffBundle
    }
}
