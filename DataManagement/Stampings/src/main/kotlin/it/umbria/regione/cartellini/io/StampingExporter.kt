package it.umbria.regione.cartellini.io

import it.umbria.regione.cartellini.*
import it.umbria.regione.cartellini.model.DataModel
import it.umbria.regione.cartellini.model.Employee
import it.umbria.regione.cartellini.model.Stamping
import it.umbria.regione.model.ModelDate
import it.umbria.regione.model.ModelTime
import it.umbria.regione.model.sumOfTime
import org.apache.poi.ss.usermodel.*
import java.io.File

class StampingsExporter(private val dm: DataModel) {

    private val extraWorkCodes = "STR/710/DOM/063"

    private lateinit var timeStyle: CellStyle
    private lateinit var dateStyle: CellStyle
    private lateinit var numberStyle: CellStyle
    private lateinit var headerStyle: CellStyle
    private lateinit var tooMuchHours: CellStyle
    private lateinit var tooMuchDays: CellStyle
    private lateinit var tooLessHours: CellStyle
    private lateinit var error: CellStyle

    fun exportStampings(output: File) {
        dm.cleanUnfinishedStampings()
        WorkbookFactory.create(true).use { workbook ->
            createStyles(workbook)
            createDataSheet(workbook, dm.stampings)
            createAggregationSheet(workbook, dm.stampings)
            dm.employees.forEach { createEmployeeSheet(workbook, it) }
            output.parentFile.mkdirs()
            output.outputStream().use { o ->
                workbook.write(o)
            }
        }
    }

    private fun createStyles(workbook: Workbook) {
        val createHelper = workbook.creationHelper
        headerStyle = workbook.createCellStyle().apply {
            alignment = HorizontalAlignment.CENTER
            val font = workbook.createFont()
            font.bold = true
            setFont(font)
        }
        timeStyle = workbook.createCellStyle().apply {
            //dataFormat = createHelper.createDataFormat().getFormat("""[>1]d"d" hh:mm;hh:mm"""")
            dataFormat = createHelper.createDataFormat().getFormat("hh:mm")
        }
        dateStyle = workbook.createCellStyle().apply {
            dataFormat = createHelper.createDataFormat().getFormat("dd/MM/yyyy")
        }
        numberStyle = workbook.createCellStyle().apply {
            dataFormat = createHelper.createDataFormat().getFormat("0")
        }
        tooMuchHours = workbook.createCellStyle().apply {
            cloneStyleFrom(timeStyle)
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.CORAL.index
        }
        tooLessHours = workbook.createCellStyle().apply {
            cloneStyleFrom(timeStyle)
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.LIGHT_YELLOW.index
        }
        tooMuchDays = workbook.createCellStyle().apply {
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.RED.index
        }
        val font = workbook.createFont()
        font.color = IndexedColors.WHITE.index
        error = workbook.createCellStyle().apply {
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.RED.index
            alignment = HorizontalAlignment.CENTER
            setFont(font)
        }
    }

    private fun createDataSheet(workbook: Workbook, stampings: List<Stamping>) {
        val sheet = workbook.createSheet("Dati")
        val header = sheet.createRow(0)
        header.createRow(
            "Dipendente",
            "Giorno",
            "Data ingresso",
            "Ora ingresso",
            "Data uscita",
            "Ora uscita",
            "Durata turno",
            "Durata turno in secondi",
            "Ore aggiuntive ($extraWorkCodes)",
            "Ore aggiuntive in secondi",
            "Scostamento",
            "Scostamento in secondi",
            "Riposo"
        )
        var lastStamp: Stamping? = null
        for ((index, stamp) in stampings.sorted().withIndex()) {
            if (!stamp.isComplete)
                continue
            val row = sheet.createRow(index + 1)
            var c = 0
            row.createCell(c++).setCellValue(stamp.employee.name)
            row.createCell(c++).setCellValue(stamp.inDate.dayOfTheWeek())
            row.createCell(c++).setCellValue(stamp.inDate)
            row.createCell(c++).setCellValue(stamp.inTime)
            row.createCell(c++).setCellValue(stamp.outDate)
            row.createCell(c++).setCellValue(stamp.outTime)
            row.createCell(c++).apply {
                setCellValue(stamp.duration)
                if (stamp.duration > maxDailyShiftDuration)
                    cellStyle = tooMuchHours
                else if (stamp.duration < minTimeBeforeNextShift)
                    cellStyle = tooLessHours
            }
            row.createCell(c++).apply {
                cellStyle = numberStyle
                setCellValue(stamp.duration.toSeconds.toDouble())
            }
            row.createCell(c++).setCellValue(stamp.totalSurplusHours.takeIf { it.isPositive })
            row.createCell(c++).apply {
                cellStyle = numberStyle
                val sec = stamp.totalSurplusHours.takeIf { it.isPositive }?.toSeconds?.toDouble()
                if (sec != null)
                    setCellValue(sec)
            }
            row.createCell(c++).apply {
                setCellValue(stamp.deviation.takeIf { it.isPositive })
                if (stamp.deviation.isPositive)
                    cellStyle = tooMuchHours
            }
            row.createCell(c++).apply {
                cellStyle = numberStyle
                val sec = stamp.deviation.takeIf { it.isPositive }?.toSeconds?.toDouble()
                if (sec != null)
                    setCellValue(sec)
            }
            if (lastStamp != null && lastStamp.employee == stamp.employee) {
                if (!satisfiedRest(lastStamp, stamp)) {
                    row.createCell(c).apply {
                        setCellValue("No")
                        cellStyle = error
                    }
                }
            }
            lastStamp = stamp
        }
        for (i in 0..sheet.lastRowNum)
            sheet.autoSizeColumn(i)
    }

    private fun createAggregationSheet(workbook: Workbook, stampings: List<Stamping>) {
        val sheet = workbook.createSheet("Aggregato")
        val header = sheet.createRow(0)
        header.createRow(
            "Dipendente", "TotaleGiorniNoRiposo",
            "TotaleOreOltreMaxAlGiorno",
            "TotaleOreOltreMaxSettimana",
            "TotaleOreAggiuntive ($extraWorkCodes)",
            "TotaleScostamento"
        )
        var index = 1
        for ((empl, list) in stampings.groupByEmployee()) {
            val row = sheet.createRow(index++)
            var c = 0
            row.createCell(c++).setCellValue(empl.name)
            row.createCell(c++).apply {
                val g = list.numberOfUnrestedDays().toDouble()
                setCellValue(g)
                if (g > 0)
                    cellStyle = tooMuchDays
            }
            row.createCell(c++).setCellValue(list.groupByDate().values.sumPositiveExceeding {
                it.numberOfHoursAboveMax(maxDailyShiftDuration)
            })
            row.createCell(c++).setCellValue(list.groupByWeek().values.sumPositiveExceeding {
                it.totalDuration() - maxWeeklyShiftDuration
            })
            row.createCell(c++).setCellValue(list.sumOfTime { it.totalSurplusHours })
            row.createCell(c).apply {
                val s = list.sumOfTime { it.deviation }
                setCellValue(s)
                if (s.isPositive)
                    cellStyle = tooMuchHours
            }
        }
        for (i in 0..sheet.lastRowNum)
            sheet.autoSizeColumn(i)
    }


    private fun createEmployeeSheet(workbook: Workbook, employee: Employee) {
        val sheet = workbook.createSheet(employee.name)
        val r = createDataForMonths(sheet, employee) + 2
        createDataForWeeks(sheet, employee, r)
        for (i in 0..sheet.lastRowNum)
            sheet.autoSizeColumn(i)
    }

    private fun createDataForMonths(sheet: Sheet, employee: Employee): Int {
        val header = sheet.createRow(0)
        header.createRow(
            "Mese",
            "OreTotali",
            "OreAggiuntive ($extraWorkCodes)",
            "Scostamento",
            "GiorniNoRiposo"
        )
        var r = 1
        employee.stampings.groupByMonth().forEach { (month, list) ->
            sheet.setValues(r++, month.toString(), list)
        }

        sheet.setValues(r++, "Totale", employee.stampings)
        return r
    }

    private fun createDataForWeeks(sheet: Sheet, employee: Employee, row: Int) {
        var r = row
        val header = sheet.createRow(r++)
        header.createRow(
            "Settimana",
            "OreTotali",
            "OreAggiuntive ($extraWorkCodes)",
            "Scostamento",
            "GiorniNoRiposo"
        )
        employee.stampings.groupByWeek().forEach { (week, list) ->
            sheet.setValues(r++, week.toString(), list) { o ->
                o > maxWeeklyShiftDuration
            }
        }
    }

    private fun Sheet.setValues(
        r: Int,
        type: String,
        list: List<Stamping>,
        isError: (ModelTime) -> Boolean = { false }
    ) {
        val row = createRow(r)
        var c = 0
        row.createCell(c++).setCellValue(type)
        row.createCell(c++).apply {
            val t = list.totalDuration()
            setCellValue(t)
            if (isError(t))
                cellStyle = tooMuchHours
        }
        row.createCell(c++).setCellValue(list.sumOfTime { it.totalSurplusHours })
        row.createCell(c++).apply {
            val s = list.sumOfTime { it.deviation }
            setCellValue(s)
            if (s.isPositive)
                cellStyle = tooMuchHours
        }
        row.createCell(c).apply {
            val g = list.numberOfUnrestedDays().toDouble()
            setCellValue(g)
            if (g > 0)
                cellStyle = tooMuchDays
        }
    }

    private fun Row.createRow(vararg text: String) {
        for ((c, t) in text.withIndex()) {
            createCell(c).apply {
                setCellValue(t)
                cellStyle = headerStyle
            }
        }
    }

    private fun Cell.setCellValue(ora: ModelTime?) {
        if (ora != null) {
            cellStyle = timeStyle
            setCellValue(ora.toString())
//            if (ora > giorno) {
//                println("Ora superiore al massimo: $ora")
//                cellStyle = tooMuchHours
//            }
        } else {
            setCellValue("")
            cellStyle = timeStyle
        }
    }

    private fun Cell.setCellValue(data: ModelDate) {
        cellStyle = dateStyle
        setCellValue(data.toLocalDate())
    }
}
