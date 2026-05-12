package it.umbria.regione.cdc.db

import it.umbria.regione.config.DatabaseParameters
import it.umbria.regione.db.DBTable
import it.umbria.regione.db.XLSDatabase

class CdCDatabase(database: DatabaseParameters) : XLSDatabase(database) {

    override val tables: Array<out DBTable>
        get() = arrayOf(table)

}
