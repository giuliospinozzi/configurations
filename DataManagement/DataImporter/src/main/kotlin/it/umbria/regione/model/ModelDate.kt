package it.umbria.regione.model

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

data class ModelDate(
    val day: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    val month: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val year: Int = Calendar.getInstance().get(Calendar.YEAR)
) : Comparable<Any> {

    fun toDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, day, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun toLocalDate(): LocalDate {
        return LocalDate.of(year, month, day)
    }

    override operator fun compareTo(other: Any): Int {
        if (other !is ModelDate)
            throw IllegalArgumentException("You must compare two ModelDate objects")
        return toDate().compareTo(other.toDate())
    }

    fun dayOfTheWeek(): Day {
        val cal = Calendar.getInstance()
        cal.time = toDate()
        return days[cal.get(Calendar.DAY_OF_WEEK) - 1]
    }

    fun daysBetween(other: ModelDate): Int {
        val d1 = toDate()
        val d2 = other.toDate()
        val diff = d1.time - d2.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    override fun toString(): String {
        return "${day.pad()}/${month.pad()}/$year"
    }

    fun toInternationalString(): String {
        return "$year-$month-$day"
    }

    fun toSimpleString(): String {
        return "$year${month.pad()}${day.pad()}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelDate

        if (day != other.day) return false
        if (month != other.month) return false
        if (year != other.year) return false

        return true
    }

    override fun hashCode(): Int {
        var result = day
        result = 31 * result + month
        result = 31 * result + year
        return result
    }
}

fun String.toModelDate(): ModelDate {
    val pattern = if (this.contains("-"))
        "yyyy-MM-dd"
    else if (this.contains("/"))
        "dd/MM/yyyy"
    else
        "yyyyMMdd"
    val formatter = SimpleDateFormat(pattern)
    val cal = Calendar.getInstance()
    cal.time = formatter.parse(this)
    return ModelDate(
        year = cal.get(Calendar.YEAR),
        month = cal.get(Calendar.MONTH) + 1,
        day = cal.get(Calendar.DAY_OF_MONTH)
    )
}

fun Date.toModelDate(): ModelDate {
    val cal = Calendar.getInstance()
    cal.time = this
    return ModelDate(
        year = cal.get(Calendar.YEAR),
        month = cal.get(Calendar.MONTH) + 1,
        day = cal.get(Calendar.DAY_OF_MONTH)
    )
}
