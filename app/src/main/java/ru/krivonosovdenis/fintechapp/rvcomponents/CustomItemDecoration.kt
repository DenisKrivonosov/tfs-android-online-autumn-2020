package ru.krivonosovdenis.fintechapp.rvcomponents

import android.graphics.Canvas
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

//В целом пока не разобрался как правильно рисовать дивайдеры. Покопал немного, но реализовать
//не вышло. Здесь общий набросок для дальнейшей реализации
interface CustomItemDecorationAdapter {
    fun drawCustomDivider(
        position: Int,
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    )
}

class CustomItemDecoration(val rvAdapter: CustomItemDecorationAdapter) :
    RecyclerView.ItemDecoration() {
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        //Не знаю, правильный ли это задел на будущее - здесь прокидываю реализацию onDraw
        //Для каждой вьюшки в PostsFeedAdapter.
        parent.adapter?.let { adapter ->
            parent.children
                .forEach { view ->
                    val childAdapterPosition = parent.getChildAdapterPosition(view)
                        .let { if (it == RecyclerView.NO_POSITION) return else it }
                    if (childAdapterPosition != adapter.itemCount - 1) {
                        rvAdapter.drawCustomDivider(childAdapterPosition, canvas, parent, state)
                    }
                }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }
}
