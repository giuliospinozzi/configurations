package it.umbria.regione.cartellini.db

import it.umbria.regione.db.*
import it.umbria.regione.model.days

val cartelliniTable = DBTable("Cartellini").apply {
    columns.apply {
        add(ID)
        add(IMPORT_DATE)
        add(DBColumn("ragioneSocialeEnte", DBVarchar, notNull = true))
        add(DBColumn("sede", DBChar(8), notNull = true))
        add(DBColumn("descrSede", DBVarchar, notNull = true))
        add(DBColumn("UO", DBVarchar, notNull = true))
        add(DBColumn("cognomeNome", DBVarchar, notNull = true))
        add(DBColumn("inizioSede", DBDate, notNull = true))
        add(DBColumn("data", DBDate, notNull = true))
        add(DBColumn("gg", DBEnum(*days), notNull = true))
        add(DBColumn("orario", DBVarchar, notNull = true))
        add(DBColumn("E1", DBTime))
        add(DBColumn("U1", DBTime))
        add(DBColumn("E2", DBTime))
        add(DBColumn("U2", DBTime))
        add(DBColumn("E3", DBTime))
        add(DBColumn("U3", DBTime))
        add(DBColumn("E4", DBTime))
        add(DBColumn("U4", DBTime))
        add(DBColumn("oreTeo", DBTime))
        add(DBColumn("CAU1", DBVarchar))
        add(DBColumn("CAU2", DBVarchar))
        add(DBColumn("CAU3", DBVarchar))
        add(DBColumn("CAU4", DBVarchar))
        add(DBColumn("CAU5", DBVarchar))
        add(DBColumn("CAU6", DBVarchar))
        add(DBColumn("CAU7", DBVarchar))
        add(DBColumn("CAU8", DBVarchar))
        add(DBColumn("CAU9", DBVarchar))
        add(DBColumn("anomalia", DBVarchar))
        add(DBColumn("EV1", DBVarchar))
        add(DBColumn("EV2", DBVarchar))
        add(DBColumn("EV3", DBVarchar))
    }
}
