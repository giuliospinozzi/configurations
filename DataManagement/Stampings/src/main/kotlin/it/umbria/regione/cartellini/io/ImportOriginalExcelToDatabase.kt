package it.umbria.regione.cartellini.io

import it.umbria.regione.cartellini.StampingsParameters
import it.umbria.regione.cartellini.StampingsParameters.importDate
import it.umbria.regione.cartellini.db.StampingDatabase
import it.umbria.regione.cartellini.db.cartelliniTable
import it.umbria.regione.io.ExcelToDatabaseImporter
import it.umbria.regione.io.date
import it.umbria.regione.io.string
import it.umbria.regione.io.time
import it.umbria.regione.model.HeaderIndex

class ImportOriginalExcelToDatabase(db: StampingDatabase) : ExcelToDatabaseImporter(db) {

    fun importFile(p: StampingsParameters) {
        prepareDatabase(p)
        readWorkbook(p) { index, row ->
            cartelliniTable.createValues(index) { values ->
                values.setFromRow(row, "Ragione sociale ente") { it.string() }
                values.setFromRow(row, "Sede", "sede") { it.string() }
                values.setFromRow(row, "Descr sede") { it.string() }
                values.setFromRow(row, "UO") { it.string() }
                values.setFromRow(row, "Cognome nome") { it.string() }
                values.setFromRow(row, "Inizio sede") { it.date() }
                values.setFromRow(row, "Data") { it.date() }
                values.setFromRow(row, "gg") { it.string() }
                values.setFromRow(row, "Orario") { it.string() }
                values.setFromRow(row, "E1") { it.time() }
                values.setFromRow(row, "U1") { it.time() }
                values.setFromRow(row, "E2") { it.time() }
                values.setFromRow(row, "U2") { it.time() }
                values.setFromRow(row, "E3") { it.time() }
                values.setFromRow(row, "U3") { it.time() }
                values.setFromRow(row, "E4") { it.time() }
                values.setFromRow(row, "U4") { it.time() }
                values.setFromRow(row, "Ore teo") { it.time() }
                values.setFromRow(row, "CAU1") { it.string() }
                values.setFromRow(row, "CAU2") { it.string() }
                values.setFromRow(row, "CAU3") { it.string() }
                values.setFromRow(row, "CAU4") { it.string() }
                values.setFromRow(row, "CAU5") { it.string() }
                values.setFromRow(row, "CAU6") { it.string() }
                values.setFromRow(row, "CAU7") { it.string() }
                values.setFromRow(row, "CAU8") { it.string() }
                values.setFromRow(row, "CAU9") { it.string() }
                values.setFromRow(row, "Anomalia") { it.string() }
                values.setFromRow(row, "EV1") { it.string() }
                values.setFromRow(row, "EV2") { it.string() }
                values.setFromRow(row, "EV3") { it.string() }
                db.insertData(cartelliniTable, values = values, importDate = importDate)
                0
            }
        }
        db.prepareInsert(cartelliniTable).executeBatch()
    }

    override val fileHeader: HeaderIndex by lazy {
        HeaderIndex(
            "Ragione sociale ente" to 1,
            "Sede" to 2,
            "Descr sede" to 3,
            "UO" to 5,
            "Cognome nome" to 8,
            "Inizio sede" to 9,
            "Data" to 10,
            "gg" to 11,
            "Orario" to 12,
            "E1" to 13,
            "U1" to 14,
            "E2" to 15,
            "U2" to 16,
            "E3" to 17,
            "U3" to 18,
            "E4" to 19,
            "U4" to 20,
            "Ore teo" to 21,
            "CAU1" to 22,
            "CAU2" to 23,
            "CAU3" to 24,
            "CAU4" to 25,
            "CAU5" to 26,
            "CAU6" to 27,
            "CAU7" to 28,
            "CAU8" to 29,
            "CAU9" to 30,
            "Anomalia" to 31,
            "EV1" to 34,
            "EV2" to 35,
            "EV3" to 36,
        )
    }
}
