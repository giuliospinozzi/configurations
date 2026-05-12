package it.umbria.regione.model

import java.text.SimpleDateFormat
import java.util.*

data class ModelDateTime(
    val date: ModelDate = ModelDate(),
    val time: ModelTime = ModelTime.now
) : Comparable<Any> {

    override operator fun compareTo(other: Any): Int {
        if (other !is ModelDateTime)
            throw IllegalArgumentException("You must compare two ModelDateTime objects")
        return date.compareTo(other.date).let { if (it != 0) it else time.compareTo(other.time) }
    }

    override fun toString(): String {
        return "$date $time"
    }

    fun toInternationalString(): String {
        return "${date.toInternationalString()}T$time"
    }

    fun toTimeStamp(): java.sql.Timestamp {
        return java.sql.Timestamp(toJavaDate().time)
    }

    fun toJavaDate(): Date {
        return Date(date.year - 1900, date.month - 1, date.day, time.hours, time.minutes, 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelDateTime

        if (date != other.date) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + time.hashCode()
        return result
    }
}

fun String.toModelDateTime(): ModelDateTime {
    if (this.contains("T")) {
        //2024-12-25T23:59
        val split = this.split("T")
        return ModelDateTime(
            date = split[0].toModelDate(),
            time = split[1].toModelTime()
        )
    } else if (this.contains("-") || this.contains("/")) // only date in any format
        return ModelDateTime(date = this.toModelDate())
    else if (this.contains(":")) // only time
        return ModelDateTime(time = this.toModelTime())
    else
        throw UnsupportedOperationException("Invalid date time format: $this")
}
