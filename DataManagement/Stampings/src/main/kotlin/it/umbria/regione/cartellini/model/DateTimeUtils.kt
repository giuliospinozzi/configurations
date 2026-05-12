package it.umbria.regione.cartellini.model

import it.umbria.regione.cartellini.maxDailyShiftDuration
import it.umbria.regione.cartellini.maxWeeklyShiftDuration
import it.umbria.regione.model.ModelTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

fun Calendar.numberOfWorkingDays(): Int {
    val startDate = LocalDate.of(get(Calendar.YEAR), get(Calendar.MONTH) + 1, 1)
    val endDate = LocalDate.of(
        get(Calendar.YEAR),
        get(Calendar.MONTH) + 1,
        getActualMaximum(Calendar.DAY_OF_MONTH)
    )
    return ChronoUnit.DAYS.between(startDate, endDate)
        .let { days -> generateSequence(startDate) { it.plusDays(1) }.take(days.toInt()).toList() }
        .count { it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY }
}

fun ModelTime.checkDay(): Boolean {
    return this <= maxDailyShiftDuration
}

fun ModelTime.checkWeek(): Boolean {
    return this <= maxWeeklyShiftDuration
}
