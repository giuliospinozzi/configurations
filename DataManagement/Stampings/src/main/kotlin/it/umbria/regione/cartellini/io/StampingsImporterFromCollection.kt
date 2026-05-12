package it.umbria.regione.cartellini.io

import it.umbria.regione.cartellini.model.DataModel
import it.umbria.regione.cartellini.model.Employee
import it.umbria.regione.cartellini.model.Stamping
import it.umbria.regione.io.time
import it.umbria.regione.model.ModelTime

class StampingsImporterFromCollection(private val dm: DataModel) {

    private var lastStamp: Stamping? = null
    private var lastEmpl: Employee? = null

    fun importNextStampings(r: DataReader) {
        try {
            val employee = dm.findEmployeeByName(r.getEmployee())
            if (lastEmpl != null && lastStamp?.outTime == null && lastEmpl != employee) {
                println("Error at current row: employee $lastEmpl did not close the stampings on ${lastStamp!!.inDate}")
                lastStamp = null
            }
            lastEmpl = employee
            val data = r.getShiftDate()
            for (o in r.numberOfStamps step 2) {
                val oraI = r.getTime(o)
                val oraU = r.getTime(o + 1)
                if (oraI == null && oraU == null)
                    continue
                if (lastStamp == null || lastStamp!!.outTime != null) {
                    if (oraI == null) {
                        println("Error at current row: missing entry time")
                        continue
                    }
                    lastStamp = Stamping(
                        employee = employee,
                        inDate = data,
                        inTime = oraI,
                        outTime = oraU
                    )
                    employee.stampings.add(lastStamp!!)
                } else {
                    lastStamp!!.outDate = data
                    lastStamp!!.outTime = oraU
                }
            }
            if (lastStamp != null)
                checkReasons(r, lastStamp!!)
        } catch (e: Exception) {
            println("Error at current row: $e")
            throw e
        }
    }


    private fun checkReasons(r: DataReader, lastStamp: Stamping) {
        val festivo = r.findIfHoliday()
        if (festivo) {
            val deviation = r.findDeviation()
            lastStamp.overwork += deviation!!
        } else {
            val extraWork = r.findExtraWork()
            if (extraWork != null)
                lastStamp.overwork += extraWork
            else if (r.findAuthorizedSurplus()) {
                lastStamp.authorizedSurplus = true
            }
            val deviation = r.findDeviation()
            if (deviation != null)
                lastStamp.deviation += deviation
        }
    }

    private fun DataReader.findIfHoliday(): Boolean {
        for (col in indexFromReason until columnCount) {
            val v = getString(col)
            if (v.startsWith("PRESTAZIONE LAVORATIVA DI DOMENICA"))
                return true
        }
        return false
    }

    private fun DataReader.findDeviation(): ModelTime? {
        for (col in indexFromReason until columnCount) {
            val v = getString(col)
            if (v.startsWith("SCOST+"))
                return v.time("SCOST+")
//        if (cel.stringCellValue.startsWith("SCOST-"))
//            return cel.ora("SCOST-")?.invert()
        }
        return null
    }

    private fun DataReader.findExtraWork(): ModelTime? {
        for (col in indexFromReason until columnCount) {
            val v = getString(col)
            for (s in arrayOf("STR AUT", "710.", "710")) {
                if (v.startsWith(s))
                    return v.time(s)
            }
        }
        return null
    }

    private fun DataReader.findAuthorizedSurplus(): Boolean {
        for (col in indexFromReason until columnCount) {
            val v = getString(col)
            if (v.startsWith("ECCEDENZA AUTORIZZATA"))
                return true
        }
        return false

    }
}
