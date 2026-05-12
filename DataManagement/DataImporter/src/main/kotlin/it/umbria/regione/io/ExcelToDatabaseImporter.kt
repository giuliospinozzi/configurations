package it.umbria.regione.io

import it.umbria.regione.config.DataImporterParameters
import it.umbria.regione.db.DBTable
import it.umbria.regione.db.DBValues
import it.umbria.regione.db.XLSDatabase
import it.umbria.regione.model.HeaderIndex
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.plibrary.util.ProgressCalculator

abstract class ExcelToDatabaseImporter(protected val db: XLSDatabase) {

    private val calc = ProgressCalculator()

    protected open fun prepareDatabase(p: DataImporterParameters) {
        if (p.dropDatabase) {
            calc.printGraphicOutput("Dropping tables")
            db.dropTables()
        }
        db.checkTableExists()
    }

    protected fun readWorkbook(p: DataImporterParameters, sheetNumber: Int = 0, reader: (Int, Row) -> Unit) {
        calc.printGraphicOutput("Reading workbook/$sheetNumber")
        p.openInputStream().use { i ->
            val workbook = WorkbookFactory.create(i)
            val sheet = workbook.getSheetAt(sheetNumber)
            calc.maximum = sheet.lastRowNum.toLong()
            for ((index, row) in sheet.withIndex()) {
                if (index == 0) {
                    calc.printGraphicOutput("Reading header row")
                    prepareHeader(sheetNumber, row)
                    continue
                }
                try {
                    calc.doStep()
                    calc.printGraphicOutput("Reading workbook/$sheetNumber")
                    reader(index, row)
                } catch (e: Exception) {
                    println("Error at row $index")
                    e.printStackTrace()
                    throw e
                }
            }
            calc.done()
        }
    }

    protected open fun prepareHeader(sheetNumber: Int = 0, row: Row) {}

    protected fun DBTable.createValues(index: Int, values: DBTable.(DBValues) -> Int): Int {
        try {
            val v = DBValues(this)
            return values(v)
        } catch (e: Exception) {
            println("Error at row $index")
            e.printStackTrace()
            throw e
        }
    }

    protected fun DBValues.setFromRow(
        row: Row, xlsLabel: String, dbLabel: String = xlsLabel.toDatabaseLabel(),
        getter: (Cell) -> Any?
    ) {
        val cell = row.getCell(fileHeader[xlsLabel])
        this[dbLabel] = cell?.let(getter)
    }

    protected fun DBValues.setFromRow(
        row: Row, index: Int, dbLabel: String, getter: (Cell) -> Any?
    ) {
        val cell = row.getCell(index)
        this[dbLabel] = cell?.let(getter)
    }

    protected fun Row.getCell(xlsLabel: String): Cell {
        val i = fileHeader[xlsLabel]
        require(i >= 0) { "Label $xlsLabel not found" }
        return getCell(i) ?: createCell(i)
    }

    protected abstract val fileHeader: HeaderIndex
}

fun String.toDatabaseLabel(): String {
    val split = this.split("\\s+|_".toRegex())
    return buildString {
        for (i in split.indices) {
            val s = split[i]
            if (s.count { it.isLowerCase() } == 0)
                append(s)
            else
                append(if (i == 0) s.lowercase() else s.replaceFirstChar(Char::uppercase))
        }
    }
}
