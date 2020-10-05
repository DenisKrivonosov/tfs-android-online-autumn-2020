package ru.krivonosovdenis.fintechapp.rvcomponents

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperAdapter {
    fun onItemDismiss(position: Int)
    fun onItemLiked(position: Int)
}

class ItemTouchHelperCallback(private val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.SimpleCallback(
    0,
    START or END
) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == END) {
            adapter.onItemLiked(viewHolder.adapterPosition)
        }
        if (direction == START) {
            adapter.onItemDismiss(viewHolder.adapterPosition)
        }
    }
}
