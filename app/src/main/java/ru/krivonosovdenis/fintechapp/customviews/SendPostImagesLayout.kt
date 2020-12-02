package ru.krivonosovdenis.fintechapp.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.children
import androidx.core.view.isGone
import kotlin.math.max

//Немного модернизированная реализация кастомного флексбокса с семинара.
// Дополнительно учел случай разных по размеру элементов и случай невидимых элементов
//Случай, в котором размер ребенка больше размера родителя не был реализован.
class SendPostImagesLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleRAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleRAttr) {

    init {
        setWillNotDraw(true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val height = 0
        var currentRowWidth = 0
        var currentRowHeight = 0
        var heightWithoutLastRow = 0
        children.forEach { child ->
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, height)
            currentRowWidth += child.measuredWidth
            if (currentRowWidth > desiredWidth) {
                currentRowWidth = child.measuredWidth
                heightWithoutLastRow += currentRowHeight
                currentRowHeight = child.measuredHeight
            } else {
                currentRowHeight = max(currentRowHeight, child.measuredHeight)
            }
        }
        setMeasuredDimension(
            desiredWidth,
            resolveSize(heightWithoutLastRow + currentRowHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentLeft = paddingLeft
        var currentTop = paddingTop
        var currentRowHeight = 0
        children.forEach iterator@{ child ->
            if (child.isGone) {
                return@iterator
            }
            val currentRight = currentLeft + child.measuredWidth
            currentRowHeight = max(currentRowHeight, child.measuredHeight)
            if (currentRight > measuredWidth) {
                currentLeft = paddingLeft
                currentTop += currentRowHeight
                currentRowHeight = child.measuredHeight
            }
            child.layout(currentLeft, currentTop, currentRight, currentTop + child.measuredHeight)
            currentLeft += child.measuredWidth
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?) =
        MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    override fun generateDefaultLayoutParams() = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
}
