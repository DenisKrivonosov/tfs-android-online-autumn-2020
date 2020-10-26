package ru.krivonosovdenis.fintechapp.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.*
import kotlinx.android.synthetic.main.soc_network_post_details.view.*
import ru.krivonosovdenis.fintechapp.R
import kotlin.math.max

class SocNetworkPostDetails @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleRAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleRAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.soc_network_post_details, this, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        var totalHeight = paddingTop
        measureChildWithMargins(posterAvatar, widthMeasureSpec, 0, heightMeasureSpec, totalHeight)
        measureChildWithMargins(posterName, widthMeasureSpec, 0, heightMeasureSpec, totalHeight)
        measureChildWithMargins(postDate, widthMeasureSpec, 0, heightMeasureSpec, totalHeight)

        //В итоге у меня не получилось сделать так, чтобы  measureChildWithMargins заработал
        // корректно. Что-то я делаю не так. Поэтому при измерении размеров детей лейаута а также
        // в самом методe onLayout я дополнительно учитываю марджины элемента. Неудобно и некрасиво,
        // но что поделать
        val poserAvatarHeight =
            posterAvatar.measuredHeight + posterAvatar.marginTop + posterAvatar.marginBottom
        val posterNameHeight =
            posterName.measuredHeight + posterName.marginTop + posterName.marginBottom
        val postDateHeight = postDate.measuredHeight + postDate.marginTop + postDate.marginBottom

        totalHeight += max(poserAvatarHeight, posterNameHeight + postDateHeight)
        //Так как в дальнейшем посты из вк могут приходить без текста или без картинки, надо этот
        //момент учесть заранее

        measureChildWithMargins(postText, widthMeasureSpec, 0, heightMeasureSpec, totalHeight)
        totalHeight += postText.measuredHeight + postText.marginTop + postText.marginBottom

        if (!postImage.isGone) {
            measureChildWithMargins(postImage, widthMeasureSpec, 0, heightMeasureSpec, totalHeight)
            totalHeight += postImage.measuredHeight + postImage.marginTop + postImage.marginBottom
        }
        measureChildWithMargins(
            postActionsDivider,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            totalHeight
        )
        totalHeight += postActionsDivider.measuredHeight + postActionsDivider.marginTop + postActionsDivider.marginBottom
        measureChildWithMargins(postActionLike, widthMeasureSpec, 0, heightMeasureSpec, totalHeight)
        totalHeight += postActionLike.measuredHeight + postActionLike.marginTop + postActionLike.marginBottom
        measureChildWithMargins(
            postLikesCounter,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            totalHeight
        )
        setMeasuredDimension(desiredWidth, resolveSize(totalHeight, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //В дальнейшем в коде было добавлено много переменных.
        // По факту - для каждой вьюхи во вьюгруппе создано по 4 переменных, отвечающих за позицию элемента
        // Возможно, это выглдяит некрасиво и так делать не стоит, но учитывая ситуацию с марджинами -
        // этот код в дальнейшем легче править, так как у нас есть сохраненные координаты всех элементов

        //Также текущая реадизация нашей вьюгруппы не поддерживает RTL, поэтому в верстке мы
        // используем атрибуты marginLeft и marginRight не смотря на то, что студия рекомендует
        // использовать marginStart и marginEnd. Можно все переписать на marginStart и marginEnd,
        //студия прекратит ругаться, но без конкретной реализации RTL это бессмысленно
        //Начальные координаты для лейаутинга внутри вьюгруппы
        var currentLeft = paddingLeft
        var currentTop = paddingTop

        // Данные измерения нужы для центрирования иконки группы и названия группы/даты поста.
        val posterAvatarTotalHeight =
            posterAvatar.marginTop + posterAvatar.measuredHeight + posterAvatar.marginBottom
        val postHeaderInfoTotalHeight =
            posterName.marginTop + posterName.measuredHeight + posterName.marginBottom +
                    postDate.marginTop + postDate.measuredHeight + postDate.marginBottom

        var shouldAddSpaceToTextHeaders = false
        var shouldAddSpaceToPosterAvatar = false
        val additionalHeaderSpace: Int
        //Так как пользователь может менять размер текста - может возникнуть ситуация, когда размер
        //хедера (имя группы + дата поста) может оказаться больше, чем размер иконки. Надо обработать
        //2 случая. Первый - когда размер иконки с марджинами больше общего блока (имя группы + дата)
        if (posterAvatarTotalHeight > postHeaderInfoTotalHeight) {
            shouldAddSpaceToTextHeaders = true
            additionalHeaderSpace = (posterAvatarTotalHeight - postHeaderInfoTotalHeight) / 2
        }
        // И второй - когда размер аватара поста меньше. дополнительно центрируем его
        else {
            shouldAddSpaceToPosterAvatar = true
            additionalHeaderSpace = (postHeaderInfoTotalHeight - posterAvatarTotalHeight) / 2
        }

        val posterAvatarLeftCoordinate = currentLeft + posterAvatar.marginLeft
        val posterAvatarTopCoordinate =
            currentTop + if (shouldAddSpaceToPosterAvatar) posterAvatar.marginTop + additionalHeaderSpace else {
                posterAvatar.marginTop
            }
        val posterAvatarRightCoordinate =
            posterAvatarLeftCoordinate + posterAvatar.measuredWidth
        val posterAvatarBottomCoordinate =
            posterAvatarTopCoordinate + posterAvatar.measuredHeight
        posterAvatar.layout(
            posterAvatarLeftCoordinate,
            posterAvatarTopCoordinate,
            posterAvatarRightCoordinate,
            posterAvatarBottomCoordinate
        )
        currentLeft = posterAvatarRightCoordinate + posterAvatar.marginRight

        val posterNameLeftCoordinate = currentLeft + posterName.marginLeft
        val posterNameTopCoordinate =
            currentTop + if (shouldAddSpaceToTextHeaders) posterName.marginTop + additionalHeaderSpace else {
                posterName.marginTop
            }

        //Устанавливаю правую координату текствьюхи с правой стороны экрана с учетом марджинов
        //Проблема может возникнуть в случае длинного текста. Есть несколько возможных реализаций
        //1) Можно сделать название группы мультилайном. Так как название группы по функционалу
        // схоже с тайтлом - этот вариант не очень
        // 2) Текущая реализация. В данный момент название группы просто обрезается. Но при текущей
        // реализации не работает свойство ellipsize, так как ширина вьюхи, рассчитываемая андроидом
        //больше по факту отрисовываемой ширины. Для корректной работы ellipsize Нам нужно знать точную
        // ширину вьюхи. Реализовать можно в дальнейшем из кода. Как реализовать в текущей вьюгруппе
        //без доп хаков из кода не разобрался
        val posterNameRightCoordinate = measuredWidth - posterName.marginRight
        val posterNameBottomCoordinate =
            posterNameTopCoordinate + posterName.measuredHeight
        posterName.layout(
            posterNameLeftCoordinate,
            posterNameTopCoordinate,
            posterNameRightCoordinate,
            posterNameBottomCoordinate
        )
        currentTop = posterNameBottomCoordinate + posterName.marginBottom

        val postDateLeftCoordinate = currentLeft + postDate.marginLeft
        val postDateTopCoordinate = currentTop + postDate.marginTop
        val postDateRightCoordinate = measuredWidth - posterName.marginRight
        val postDateBottomCoordinate =
            postDateTopCoordinate + postDate.measuredHeight
        postDate.layout(
            postDateLeftCoordinate,
            postDateTopCoordinate,
            postDateRightCoordinate,
            postDateBottomCoordinate
        )
        //Чтобы текст поста не заезжал на один из элементов (аватарка или дата поста) - размещаем его
        //под элементом, расположенным ниже всего
        currentTop = max(
            posterAvatarBottomCoordinate + posterAvatar.marginBottom,
            postDateBottomCoordinate + postDate.marginBottom
        )
        currentLeft = l + paddingLeft

        if (!postImage.isGone) {
            val postImageLeftCoordinate = currentLeft + postImage.marginLeft
            val postImageTopCoordinate = currentTop + postImage.marginTop
            val postImageRightCoordinate =
                postImageLeftCoordinate + postImage.measuredWidth
            val postImageBottomCoordinate =
                postImageTopCoordinate + postImage.measuredHeight
            postImage.layout(
                postImageLeftCoordinate,
                postImageTopCoordinate,
                postImageRightCoordinate,
                postImageBottomCoordinate
            )
            currentTop = postImageBottomCoordinate + postImage.marginBottom
        }

        val postTextLeftCoordinate = currentLeft + postText.marginLeft
        val postTextTopCoordinate = currentTop + postText.marginTop
        val postTextRightCoordinate =
            postTextLeftCoordinate + postText.measuredWidth
        val postTextBottomCoordinate =
            postTextTopCoordinate + postText.measuredHeight
        postText.layout(
            postTextLeftCoordinate,
            postTextTopCoordinate,
            postTextRightCoordinate,
            postTextBottomCoordinate
        )
        currentTop = postTextBottomCoordinate + postText.marginBottom

        val postActionsDividerLeftCoordinate = currentLeft + postActionsDivider.marginLeft
        val postActionsDividerTopCoordinate = currentTop + postActionsDivider.marginTop
        val postActionsDividerRightCoordinate =
            postActionsDividerLeftCoordinate + postActionsDivider.measuredWidth
        val postActionsDividerBottomCoordinate =
            postActionsDividerTopCoordinate + postActionsDivider.measuredHeight
        postActionsDivider.layout(
            postActionsDividerLeftCoordinate,
            postActionsDividerTopCoordinate,
            postActionsDividerRightCoordinate,
            postActionsDividerBottomCoordinate
        )
        currentTop += postActionsDivider.measuredHeight + postActionsDivider.marginBottom

        currentLeft = l + paddingLeft
        val postActionLikeLeftCoordinate = currentLeft + postActionLike.marginLeft
        val postActionLikeTopCoordinate = currentTop + postActionLike.marginTop
        val postActionLikeRightCoordinate =
            postActionLikeLeftCoordinate + postActionLike.measuredWidth
        val postActionLikeBottomCoordinate =
            postActionLikeTopCoordinate + postActionLike.measuredHeight
        postActionLike.layout(
            postActionLikeLeftCoordinate,
            postActionLikeTopCoordinate,
            postActionLikeRightCoordinate,
            postActionLikeBottomCoordinate
        )

        currentLeft = postActionLikeRightCoordinate + postActionLike.marginRight
        val postLikesCounterLeftCoordinate = currentLeft + postLikesCounter.marginLeft
        val postLikesCounterTopCoordinate = currentTop + postLikesCounter.marginTop
        val postLikesCounterRightCoordinate =
            postLikesCounterLeftCoordinate + postLikesCounter.measuredWidth
        val postLikesCounterBottomCoordinate =
            postLikesCounterTopCoordinate + postLikesCounter.measuredHeight
        postLikesCounter.layout(
            postLikesCounterLeftCoordinate,
            postLikesCounterTopCoordinate,
            postLikesCounterRightCoordinate,
            postLikesCounterBottomCoordinate
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams() = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    override fun generateLayoutParams(p: LayoutParams?) = MarginLayoutParams(p)

    override fun checkLayoutParams(p: LayoutParams?) = p is MarginLayoutParams

}
