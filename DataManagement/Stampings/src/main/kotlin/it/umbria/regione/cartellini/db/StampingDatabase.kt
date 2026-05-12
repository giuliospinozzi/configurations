package it.umbria.regione.cartellini.db

import it.umbria.regione.cartellini.StampingsParameters
import it.umbria.regione.cartellini.anonymous
import it.umbria.regione.db.DBTable
import it.umbria.regione.db.DBValues
import it.umbria.regione.db.XLSDatabase
import it.umbria.regione.model.ModelDate
import it.umbria.regione.model.pad
import java.sql.PreparedStatement
import java.sql.ResultSet


class StampingDatabase(private val database: StampingsParameters) : XLSDatabase(database) {

    private val namesMap = mutableMapOf<String, String>()

    private fun nameFor(name: String): String {
        return namesMap.computeIfAbsent(name) { "Utente${(namesMap.size + 1).pad()}" }
    }

    override val tables: Array<DBTable>
        get() = arrayOf(cartelliniTable)

    override fun insertDataWithKey(table: DBTable, values: DBValues, importDate: ModelDate?): Int {
        if (anonymous)
            values["cognomeNome"] = nameFor(values["cognomeNome"] as String)
        return super.insertDataWithKey(table, values, importDate)
    }

    override fun insertData(table: DBTable, values: DBValues, importDate: ModelDate?): PreparedStatement {
        if (anonymous)
            values["cognomeNome"] = nameFor(values["cognomeNome"] as String)
        return super.insertData(table, values, importDate)
    }

    fun readData(): ResultSet {
        val sql = """SELECT *  
            |FROM ${cartelliniTable.name} ${database.toCommonWhere()} 
            |ORDER BY cognomeNome, data""".trimMargin()
        return conn.createStatement().executeQuery(sql)
    }
}
