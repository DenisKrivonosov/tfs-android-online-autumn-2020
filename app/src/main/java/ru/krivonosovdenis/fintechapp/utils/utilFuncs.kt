package ru.krivonosovdenis.fintechapp.utils

import android.content.Context
import android.util.TypedValue
import kotlin.math.abs

fun getCurrentDate(): Long {
    //Для нормально отображения постов и корректной работы примера pull to refresh
    //возвращаем хардкод дату. Основная идея - самый первый пост считать новым и не отображать
    //при дефолтном рендеринге, но отобразить при рефреше
    //    return date.time

    //10/04/2020 @ 8:19pm (UTC)
    return 1601845596

}
fun Int.dpToPx(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
)

fun humanizePostDate(postDate: Long): String {
    var secondsDiff = getCurrentDate() - postDate/1000
    if (secondsDiff < 0) {
        secondsDiff -= 1
    }

    val range1 = arrayOf(0L, 5L, 6L, 7L, 8L, 9L)
    val range2 = arrayOf(1L)
    val range3 = arrayOf(2L, 3L, 4L)
    return when {
        secondsDiff in 0..1 -> "только что"
        secondsDiff in 1..45 -> "несколько секунд назад"
        secondsDiff in 45..75 -> "минуту назад"
        secondsDiff in 75..45 * 60 && secondsDiff >= 600 && secondsDiff <= 1200 -> "${secondsDiff / 60} минут назад"
        secondsDiff in 75..45 * 60 && range1.contains(secondsDiff / 60 % 10) -> "${secondsDiff / 60} минут назад"
        secondsDiff in 75..45 * 60 && range2.contains(secondsDiff / 60 % 10) -> "${secondsDiff / 60} минуту назад"
        secondsDiff in 75..45 * 60 && range3.contains(secondsDiff / 60 % 10) -> "${secondsDiff / 60} минуты назад"
        secondsDiff in 45 * 60..75 * 60 -> "час назад"
        secondsDiff in 75 * 60..22 * 60 * 60 && secondsDiff >= 10 * 66 * 66 && secondsDiff <= 20 * 60 * 60 -> "${secondsDiff / (60 * 60)} часов назад"
        secondsDiff in 75 * 60..22 * 60 * 60 && range1.contains(secondsDiff / (60 * 60) % 10) -> "${secondsDiff / (60 * 60)} часов назад"
        secondsDiff in 75 * 60..22 * 60 * 60 && range2.contains(secondsDiff / (60 * 60) % 10) -> "${secondsDiff / (60 * 60)} час назад"
        secondsDiff in 75 * 60..24 * 60 * 60 && range3.contains(secondsDiff / (60 * 60) % 10) -> "${secondsDiff / (60 * 60)} часа назад"
        secondsDiff in 24 * 60 * 60..26 * 60 * 60 -> "день назад"
        secondsDiff in 26 * 60 * 60..360 * 24 * 60 * 60 && secondsDiff >= 10 * 24 * 60 * 60 && secondsDiff <= 20 * 24 * 60 * 60 -> "${secondsDiff / (24 * 60 * 60)} дней назад"
        secondsDiff in 26 * 60 * 60..360 * 24 * 60 * 60 && range1.contains(secondsDiff / (24 * 60 * 60) % 10) -> "${secondsDiff / (24 * 60 * 60)} дней назад"
        secondsDiff in 26 * 60 * 60..360 * 24 * 60 * 60 && range2.contains(secondsDiff / (24 * 60 * 60) % 10) -> "${secondsDiff / (24 * 60 * 60)} день назад"
        secondsDiff in 26 * 60 * 60..360 * 24 * 60 * 60 && range3.contains(secondsDiff / (24 * 60 * 60) % 10) -> "${secondsDiff / (24 * 60 * 60)} дня назад"
        secondsDiff > 360 * 24 * 60 * 60 -> "более года назад"
        else -> "$secondsDiff"
    }
}
