package it.umbria.regione.model

import java.sql.Date
import java.sql.Time
import java.util.*

typealias Day = String

val days = arrayOf("Dom", "Lun", "Mar", "Mer", "Gio", "Ven", "Sab")

fun Int.pad(): String {
    return toString().padStart(2, '0')
}

fun <T> Iterable<T>.sumOfTime(selector: (T) -> ModelTime): ModelTime {
    return map(selector).sumTime()
}

fun Iterable<ModelTime>.sumTime(): ModelTime {
    return reduceOrNull { acc, ora -> acc + ora } ?: ModelTime.empty
}

fun debug(msg: String) {
    //println(msg)
}

fun ModelDate.toSqlDate(): Date {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, day, 0, 0, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return Date(cal.timeInMillis)
}

fun ModelTime.toSqlTime(): Time {
    val cal = Calendar.getInstance()
    cal.set(0, 0, 0, hours, minutes, 0)
    cal.set(Calendar.MILLISECOND, 0);
    return Time(cal.timeInMillis)
}
