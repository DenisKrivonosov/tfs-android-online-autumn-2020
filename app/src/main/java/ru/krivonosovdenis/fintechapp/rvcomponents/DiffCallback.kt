package ru.krivonosovdenis.fintechapp.rvcomponents

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import ru.krivonosovdenis.fintechapp.dataclasses.PostRenderData


class DiffCallback : DiffUtil.ItemCallback<PostRenderData>() {
    override fun areItemsTheSame(oldItem: PostRenderData, newItem: PostRenderData): Boolean {
        return oldItem.postId == newItem.postId && oldItem.sourceId == newItem.sourceId
    }

    override fun areContentsTheSame(oldItem: PostRenderData, newItem: PostRenderData): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: PostRenderData, newItem: PostRenderData): Any? {
        val diffBundle = Bundle()
        if (oldItem.isLiked != newItem.isLiked){
            diffBundle.putBoolean("NEW_IS_LIKED",newItem.isLiked)
            diffBundle.putInt("NEW_LIKES_COUNT",newItem.likesCount)
        }
        return diffBundle
    }
}
