package it.umbria.regione.cartellini

import it.umbria.regione.cartellini.model.*
import it.umbria.regione.model.*
import org.threeten.extra.YearWeek
import java.time.LocalDate
import it.umbria.regione.model.sumOfTime

fun satisfiedRest(t1: Stamping, t2: Stamping): Boolean {
    if (t2.inDate < t1.outDate)
        throw IllegalArgumentException("Second stamping date:\n $t2 must be after the first one\n $t1")
    if (!t1.isValidShift || !t2.isValidShift)
        return true
    val rest = Stamping(
        t1.employee,
        inDate = t1.outDate,
        inTime = t1.outTime!!,
        outDate = t2.inDate,
        outTime = t2.inTime
    )
    if (rest.duration < minTimeBeforeNextShift)
        return true
    return rest.duration >= minShiftRest
}

fun List<Stamping>.satisfiedRest(): Boolean {
    return sorted().windowed(2).all { (t1, t2) -> satisfiedRest(t1, t2) }
}

fun List<Stamping>.totalDuration(): ModelTime {
    return sumOfTime { it.duration }
}

fun List<Stamping>.groupByEmployee(): Map<Employee, List<Stamping>> {
    return groupBy { it.employee }
}

fun List<Stamping>.groupByDate(): Map<ModelDate, List<Stamping>> {
    return groupBy { it.inDate }
}

fun List<Stamping>.groupByWeek(): Map<Week, List<Stamping>> {
    return groupBy {
        Week(
            YearWeek.from(
                LocalDate.of(
                    it.inDate.year,
                    it.inDate.month,
                    it.inDate.day
                )
            ).week, it.inDate.year
        )
    }
}

fun List<Stamping>.groupByMonth(): Map<Month, List<Stamping>> {
    return groupBy { Month(it.inDate.month, it.inDate.year) }.toSortedMap { o1, o2 ->
        o1.compareTo(o2)
    }
}

fun List<Stamping>.numberOfUnrestedDays(): Int {
    return sorted().windowed(2).count { (t1, t2) -> !satisfiedRest(t1, t2) }
}

fun List<Stamping>.numberOfHoursAboveMax(max: ModelTime): ModelTime {
    return map { it.duration - max }.filter { it.isPositive }.sumTime()
}

fun Collection<List<Stamping>>.sumPositiveExceeding(
    durata: (List<Stamping>) -> ModelTime = { it.totalDuration() }
): ModelTime {
    return map { durata(it) }.filter { it.isPositive }.sumTime()
}
