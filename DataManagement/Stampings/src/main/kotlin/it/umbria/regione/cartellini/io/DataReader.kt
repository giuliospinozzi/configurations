package it.umbria.regione.cartellini.io

import it.umbria.regione.io.date
import it.umbria.regione.io.time
import it.umbria.regione.model.ModelDate
import it.umbria.regione.model.ModelTime
import it.umbria.regione.model.toModelDate
import it.umbria.regione.model.toModelTime
import org.apache.poi.ss.usermodel.Row
import java.sql.ResultSet

interface DataReader {

    fun getEmployee(): String
    fun getShiftDate(): ModelDate

    fun getString(index: Int): String
    fun getDate(index: Int): ModelDate
    fun getTime(index: Int): ModelTime?

    val columnCount: Int
    val numberOfStamps: IntRange
    val indexFromReason: Int
}

class DataReaderFromResultSet(private val res: ResultSet) : DataReader {

    override fun getEmployee(): String {
        return getString(7)
    }

    override fun getShiftDate(): ModelDate {
        return getDate(9)
    }

    override fun getString(index: Int): String {
        return res.getString(index) ?: ""
    }

    override fun getDate(index: Int): ModelDate {
        return res.getDate(index).toModelDate()
    }

    override fun getTime(index: Int): ModelTime? {
        return res.getTime(index)?.toModelTime()
    }

    override val columnCount: Int
        get() = res.metaData.columnCount

    override val numberOfStamps = 12 until 19
    override val indexFromReason = 21

}

class DataReaderFromXLS(private val row: Row) : DataReader {

    override fun getEmployee(): String {
        return getString(8)
    }

    override fun getShiftDate(): ModelDate {
        return getDate(10)
    }

    override fun getString(index: Int): String {
        return row.getCell(index).stringCellValue
    }

    override fun getDate(index: Int): ModelDate {
        val cell = row.getCell(index)
        return cell.date()!!
    }

    override fun getTime(index: Int): ModelTime? {
        val cell = row.getCell(index)
        if (cell.stringCellValue.isNullOrEmpty())
            return null
        return cell.time()
    }

    override val columnCount: Int
        get() = row.lastCellNum.toInt()
    override val numberOfStamps = 13 until 20
    override val indexFromReason = 22
}
