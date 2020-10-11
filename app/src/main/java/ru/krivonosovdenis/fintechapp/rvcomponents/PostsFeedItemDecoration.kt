package ru.krivonosovdenis.fintechapp.rvcomponents

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_items_date_divider.view.*
import ru.krivonosovdenis.fintechapp.R
import ru.krivonosovdenis.fintechapp.utils.dpToPx

class PostsFeedItemDecoration :
    RecyclerView.ItemDecoration() {

    private companion object {
        const val OFFSET = 15
    }

    private var dateDecor: View? = null

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter
        if (adapter is DecorationTypeProvider) {
            parent.children.forEach { child ->
                val childPosition = parent.getChildAdapterPosition(child)
                val decorationType = adapter.getDecorationType(childPosition)

                if (decorationType is PostsFeedDecorationType.WithText) {
                    val delimiterView = getDateDecor(parent)
                    delimiterView.dividerText.text = decorationType.text
                    canvas.save()
                    canvas.translate(0f, (child.top - delimiterView.height).toFloat())
                    delimiterView.draw(canvas)
                    canvas.restore()
                }
            }
        } else {
            super.onDraw(canvas, parent, state)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) {
            return
        }
        val adapter = parent.adapter
        if (adapter is DecorationTypeProvider) {
            val decorationType = adapter.getDecorationType(position)
            outRect.top = if (decorationType is PostsFeedDecorationType.Space) {
                OFFSET.dpToPx(view.context).toInt()
            } else {
                getDateDecor(parent).height
            }
        }
    }

    private fun getDateDecor(parent: RecyclerView): View {
        return dateDecor ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_items_date_divider, parent, false).fixLayoutSizeIn(parent).also {
                dateDecor = it
            }
    }

    private fun View.fixLayoutSizeIn(parent: ViewGroup): View {
        if (layoutParams == null) {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
        val childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            layoutParams.width
        )

        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            layoutParams.height
        )

        measure(childWidth, childHeight)
        layout(0, 0, measuredWidth, measuredHeight)
        return this
    }
}
