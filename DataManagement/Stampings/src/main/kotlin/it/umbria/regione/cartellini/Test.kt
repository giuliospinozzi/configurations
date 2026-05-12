package it.umbria.regione.cartellini

import it.umbria.regione.cartellini.io.StampingsImporter
import it.umbria.regione.cartellini.model.*
import it.umbria.regione.model.ModelDate
import it.umbria.regione.model.ModelTime
import it.umbria.regione.model.sumOfTime

fun main() {
    val mr = Employee("Mario Rossi")
    val t1 = Stamping(
        employee = mr,
        inDate = ModelDate(22, 4),
        inTime = ModelTime(8, 30),
        outDate = ModelDate(22, 4),
        outTime = ModelTime(17, 0)
    )
    println(t1)
    println("Durata t1: ${t1.duration}")

    val t2 = Stamping(
        employee = mr,
        inDate = ModelDate(23, 4),
        inTime = ModelTime(8, 30),
        outDate = ModelDate(23, 4),
        outTime = ModelTime(17, 0)
    )
    println("Durata t2: ${t2.duration}")

    val list = listOf(t1, t2)
    println("Durata totale: ${list.totalDuration()}")
    println("Check day: ${list.totalDuration().checkDay()}")
    println("Check week: ${list.totalDuration().checkWeek()}")
    println("Riposo: ${satisfiedRest(t1, t2)}")

    val dm = StampingsImporter().importFromFile(inputFile)
    dm.employees.forEach { dipendente ->
        val timbrature = dipendente.stampings
        println("Dipendente: $dipendente (t: ${timbrature.size})")
        println("\tDurata totale: ${timbrature.totalDuration()}")
        println("\tMassimo giornaliero: ${timbrature.groupByDate().maxOf { it.value.totalDuration() }}")
        if (timbrature.groupByDate().values.any { !it.totalDuration().checkDay() })
            println("\t!!Giornata non rispettata!!")
        println("\tMassimo settimanale: ${timbrature.groupByWeek().maxOf { it.value.totalDuration() }}")
        if (timbrature.groupByWeek().values.any { !it.totalDuration().checkWeek() })
            println("\t!!Settimana non rispettata!!")
        if (!timbrature.satisfiedRest())
            println("\t!!Riposo non rispettato!!")
        println("\tStraordinari: ${timbrature.sumOfTime { it.totalSurplusHours }}")
    }
}
