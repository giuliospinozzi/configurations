package it.umbria.regione.cartellini.model

import it.umbria.regione.cartellini.maxDailyShiftDuration
import it.umbria.regione.cartellini.minTimeForValidShift
import it.umbria.regione.model.Day
import it.umbria.regione.model.ModelDate
import it.umbria.regione.model.ModelTime
import it.umbria.regione.model.toTime

data class Stamping(
    val employee: Employee,
    val inDate: ModelDate,
    val inTime: ModelTime,
    var outDate: ModelDate = inDate,
    var outTime: ModelTime?
) : Comparable<Stamping> {

    val inDay: Day = inDate.dayOfTheWeek()

    val outDay: Day = outDate.dayOfTheWeek()

    val days: Int
        get() = outDate.daysBetween(inDate)

    val isTheSameDay: Boolean
        get() = inDay == outDay

    val isComplete: Boolean
        get() = outTime != null

    val duration: ModelTime
        get() = (days * 24).toTime() + (outTime!! - inTime)

    val isValidShift: Boolean
        get() = duration >= minTimeForValidShift

    var overwork: ModelTime = ModelTime.empty
    var deviation: ModelTime = ModelTime.empty
    var authorizedSurplus: Boolean = false
    val totalSurplusHours: ModelTime
        get() = overwork + if (authorizedSurplus) duration - maxDailyShiftDuration else ModelTime.empty

    override fun compareTo(other: Stamping): Int {
        var i = employee.compareTo(other.employee)
        if (i == 0)
            i = inDate.compareTo(other.inDate)
        if (i == 0)
            i = inTime.compareTo(other.inTime)
        if (i == 0)
            i = outDate.compareTo(other.outDate)
        if (i == 0 && outTime != null && other.outTime != null)
            i = outTime!!.compareTo(other.outTime!!)
        return i
    }

    override fun toString(): String {
        return "Stamping(employee=$employee, " +
                "inDate=$inDate, " +
                "inTime=$inTime, " +
                "outDate=$outDate, " +
                "outTime=$outTime)"
    }
}
