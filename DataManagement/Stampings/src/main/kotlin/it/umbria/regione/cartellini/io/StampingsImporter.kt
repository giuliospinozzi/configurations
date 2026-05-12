package it.umbria.regione.cartellini.io

import it.umbria.regione.cartellini.db.StampingDatabase
import it.umbria.regione.cartellini.model.DataModel
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

class StampingsImporter {

    fun importFromDatabase(db: StampingDatabase): DataModel {
        db.readData().use { res ->
            val dm = DataModel()
            val si = StampingsImporterFromCollection(dm)
            while (res.next()) {
                si.importNextStampings(DataReaderFromResultSet(res))
            }
            return dm
        }
    }

    fun importFromFile(file: File): DataModel {
        file.inputStream().use { i ->
            val workbook = WorkbookFactory.create(i)
            val sheet = workbook.getSheetAt(0)
            val dm = DataModel()
            val si = StampingsImporterFromCollection(dm)
            sheet.withIndex().forEach { (index, row) ->
                if (index == 0)
                    return@forEach
                si.importNextStampings(DataReaderFromXLS(row))
            }
            return dm
        }
    }
}
