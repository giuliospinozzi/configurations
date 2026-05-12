package it.umbria.regione.model

import java.sql.Time
import java.util.*


class ModelTime(
    val hours: Int = 0,
    val minutes: Int = 0,
    //val seconds: Int = 0
) : Comparable<Any> {

    companion object {
        val now: ModelTime
            get() {
                val cal = Calendar.getInstance()
                return ModelTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            }
        val empty: ModelTime
            get() = ModelTime(0, 0)
    }

    operator fun plus(other: ModelTime): ModelTime {
        val minutes = this.minutes + other.minutes
        val hours = this.hours + other.hours + minutes / 60
        return ModelTime(hours, minutes % 60)
    }

    operator fun minus(other: ModelTime): ModelTime {
        val minutes = this.minutes - other.minutes
        val hours = this.hours - other.hours + if (minutes < 0) -1 else 0
        return ModelTime(
            hours = hours,
            minutes = if (minutes < 0) minutes + 60 else minutes
        )
    }

    operator fun times(other: Int): ModelTime {
        val minutes = this.minutes * other
        val hours = this.hours * other + minutes / 60
        return ModelTime(hours, minutes % 60)
    }

    override operator fun compareTo(other: Any): Int {
        if (other !is ModelTime)
            throw IllegalArgumentException("You must compare two ModelTime objects")
        return (hours * 60 + minutes) - (other.hours * 60 + other.minutes)
    }

    override fun toString(): String {
        return "${hours.pad()}:${minutes.pad()}"
    }

    fun toDate(): Date {
        return Date(0, 1, 1, hours, minutes, 0)
    }

    val toSeconds:Int
        get() = hours * 3600 + minutes * 60

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelTime

        if (hours != other.hours) return false
        if (minutes != other.minutes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hours
        result = 31 * result + minutes
        return result
    }

    fun invert(): ModelTime {
        return ModelTime(-hours, -minutes)
    }

    val isPositive: Boolean
        get() {
            return hours >= 0 && minutes >= 0 && (hours != 0 || minutes != 0)
        }
}

fun Int.toTime() = ModelTime(this, 0)

fun String.toModelTime(): ModelTime {
    //23:59
    val split = this.split(":")
    return ModelTime(
        hours = split[0].toInt(),
        minutes = split[1].toInt()
    )
}

fun Time.toModelTime(): ModelTime {
    val cal = Calendar.getInstance()
    cal.time = this
    return ModelTime(
        hours = cal.get(Calendar.HOUR_OF_DAY),
        minutes = cal.get(Calendar.MINUTE)
    )
}
