package it.umbria.regione.pnrr.io

import com.opencsv.CSVReader
import it.umbria.regione.config.DataImporterParameters
import it.umbria.regione.db.DBValues
import it.umbria.regione.db.XLSDatabase
import it.umbria.regione.io.ExcelToDatabaseImporter
import it.umbria.regione.model.HeaderIndex
import it.umbria.regione.model.toModelDate
import it.umbria.regione.pnrr.db.tableItaliaDomaniMilestones
import java.util.Date

class ImportMilestones(db: XLSDatabase) : ExcelToDatabaseImporter(db) {

    override val fileHeader: HeaderIndex
        get() = headerItaliaDomaniMilestones

    fun importCSVFile(p: DataImporterParameters) {
        db.checkTableExists()
        CSVReader(p.openInputStream().reader()).use { reader ->
            reader.readNext()
            reader.forEach { row ->
                val values = DBValues(tableItaliaDomaniMilestones)
                headerItaliaDomaniMilestones.values.forEach { header ->
                    values[header] = row[headerItaliaDomaniMilestones.get(header)]
                }
                if ((values["dataConseguimento"] as String).toModelDate().year < 2000)
                    throw RuntimeException("Not valid date ${values["dataConseguimento"]}")
                db.insertDataWithKey(tableItaliaDomaniMilestones, values)
            }
        }
    }
}
