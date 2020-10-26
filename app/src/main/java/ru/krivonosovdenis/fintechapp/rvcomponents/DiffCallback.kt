package ru.krivonosovdenis.fintechapp.rvcomponents

import androidx.recyclerview.widget.DiffUtil
import ru.krivonosovdenis.fintechapp.dataclasses.PostRenderData

class DiffCallback : DiffUtil.ItemCallback<PostRenderData>() {
    override fun areItemsTheSame(oldItem: PostRenderData, newItem: PostRenderData): Boolean {
        return oldItem.postId == newItem.postId && oldItem.sourceId == newItem.sourceId
    }

    override fun areContentsTheSame(oldItem: PostRenderData, newItem: PostRenderData): Boolean {
        return oldItem == newItem
    }
}
