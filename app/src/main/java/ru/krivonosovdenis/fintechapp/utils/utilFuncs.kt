package ru.krivonosovdenis.fintechapp.utils

import android.content.Context
import android.util.TypedValue

private const val ADDITIONAL_DELAY = 18000
fun Int.dpToPx(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
)

//Данная функция возвращает в более человеческом виде время поста. Вместо конкретного времени
//отдается более понятная для человека форма (сколько времени прошло с момента публикации)
//параметр ADDITIONAL_DELAY  - дополнительная поправка. Дело в том, что Вконтакте отдает время
//поста, которое иногда оказывается больше текущей даты в андроид устройстве. Чтобы не отображать
//пользователю, что пост из будущего - добавляем задержку по времени
//Также эту штуку я не переводил на английский язык
fun humanizeDate(currentDate: Long, postDate: Long): String {
    var secondsDiff = (currentDate + ADDITIONAL_DELAY - postDate) / 1000
    if (secondsDiff < 0) {
        secondsDiff -= 1
    }
    val range1 = arrayOf(0L, 5L, 6L, 7L, 8L, 9L)
    val range2 = arrayOf(1L)
    val range3 = arrayOf(2L, 3L, 4L)
    return when {
        secondsDiff in 0 until 15 -> "только что"
        secondsDiff in 15 until 45 -> "несколько секунд назад"
        secondsDiff in 45 until 75 -> "минуту назад"
        secondsDiff in 75 until 45 * 60 && secondsDiff >= 600 && secondsDiff <= 1200 -> "${secondsDiff / 60} минут назад"
        secondsDiff in 75 until 45 * 60 && range1.contains(secondsDiff / 60 % 10) -> "${secondsDiff / 60} минут назад"
        secondsDiff in 75 until 45 * 60 && range2.contains(secondsDiff / 60 % 10) -> "${secondsDiff / 60} минуту назад"
        secondsDiff in 75 until 45 * 60 && range3.contains(secondsDiff / 60 % 10) -> "${secondsDiff / 60} минуты назад"
        secondsDiff in 45 * 60 until 75 * 60 -> "час назад"
        secondsDiff in 75 * 60 until 22 * 60 * 60 && secondsDiff >= 10 * 66 * 66 && secondsDiff <= 20 * 60 * 60 -> "${secondsDiff / (60 * 60)} часов назад"
        secondsDiff in 75 * 60 until 22 * 60 * 60 && range1.contains(secondsDiff / (60 * 60) % 10) -> "${secondsDiff / (60 * 60)} часов назад"
        secondsDiff in 75 * 60 until 22 * 60 * 60 && range2.contains(secondsDiff / (60 * 60) % 10) -> "${secondsDiff / (60 * 60)} час назад"
        secondsDiff in 75 * 60 until 24 * 60 * 60 && range3.contains(secondsDiff / (60 * 60) % 10) -> "${secondsDiff / (60 * 60)} часа назад"
        secondsDiff in 24 * 60 * 60 until 26 * 60 * 60 -> "день назад"
        secondsDiff in 26 * 60 * 60 until 360 * 24 * 60 * 60 && secondsDiff >= 10 * 24 * 60 * 60 && secondsDiff <= 20 * 24 * 60 * 60 -> "${secondsDiff / (24 * 60 * 60)} дней назад"
        secondsDiff in 26 * 60 * 60 until 360 * 24 * 60 * 60 && range1.contains(secondsDiff / (24 * 60 * 60) % 10) -> "${secondsDiff / (24 * 60 * 60)} дней назад"
        secondsDiff in 26 * 60 * 60 until 360 * 24 * 60 * 60 && range2.contains(secondsDiff / (24 * 60 * 60) % 10) -> "${secondsDiff / (24 * 60 * 60)} день назад"
        secondsDiff in 26 * 60 * 60 until 360 * 24 * 60 * 60 && range3.contains(secondsDiff / (24 * 60 * 60) % 10) -> "${secondsDiff / (24 * 60 * 60)} дня назад"
        secondsDiff > 360 * 24 * 60 * 60 -> "более года назад"
        else -> "$secondsDiff"
    }
}
