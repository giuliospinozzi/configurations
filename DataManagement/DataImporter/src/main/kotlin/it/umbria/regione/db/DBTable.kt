package it.umbria.regione.db

import java.sql.PreparedStatement
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString
import kotlin.collections.mutableListOf

open class DBTable(val name: String) : SQLType {

    val columns = mutableListOf<DBColumn>()
    val additionalSQL = mutableListOf<SQLType>()

    private val foreignKeys: List<DBFK>
        get() = columns.filterIsInstance<DBFK>()

    operator fun get(name: String): DBColumn =
        columns.find { it.name == name } ?: throw IllegalArgumentException("Column '$name' not found")

    override val sqlName: String
        get() = name

    override val sqlDefinition: String
        get() {
            var sql = "CREATE TABLE IF NOT EXISTS `$name` (${
                columns.joinToString(prefix = "\n\t", separator = ",\n\t") { it.sqlDefinition }
            }"
            if (foreignKeys.isNotEmpty())
                sql += ",\n\t" + foreignKeys.joinToString(",\n\t") { it.sqlReference }
            if (additionalSQL.isNotEmpty())
                sql += ",\n\t" + additionalSQL.joinToString(",\n\t") { it.sqlDefinition }
            sql += "\n);"
            return sql
        }

    override fun toString(): String {
        return sqlName
    }
}

open class DBColumn(
    val name: String,
    val type: DBType = DBVarchar,
    val primaryKey: Boolean = false,
    val notNull: Boolean = false,
    val unique: Boolean = false,
    val defaultValue: String? = null
) : SQLType {

    val hasAutoincrement: Boolean
        get() = primaryKey && type == DBInt

    override val sqlDefinition: String
        get() {
            return "$name ${type.sqlType}" +
                    (if (primaryKey) " PRIMARY KEY${if (hasAutoincrement) " AUTO_INCREMENT" else ""}" else "") +
                    (if (defaultValue != null) " DEFAULT $defaultValue" else "") +
                    (if (notNull && !primaryKey) " NOT NULL" else "") +
                    (if (unique && !primaryKey) " UNIQUE" else "")
        }

    override val sqlName: String
        get() = name

    override fun toString(): String {
        return sqlDefinition
    }
}

val ID = DBColumn(name = "__id", type = DBInt, primaryKey = true)
val IMPORT_DATE = DBColumn(
    name = "dataAggiornamento",
    type = DBDate, defaultValue = "(CURRENT_DATE)", notNull = true
)

abstract class DBType(open val sqlType: String) {

    abstract fun setValue(p: PreparedStatement, index: Int, value: Any?)

    override fun toString(): String {
        return sqlType
    }
}

class DBFK(
    name: String,
    private val refTable: DBTable,
    refColName: String = ID.name,
    required: Boolean = false,
    unique: Boolean = false
) : DBColumn(name, refTable[refColName].type, notNull = required, unique = unique) {

    private val refCol: DBColumn = refTable[refColName]

    val sqlReference: String
        get() = "FOREIGN KEY ($name) REFERENCES ${refTable.name}(${refCol.name}) ON DELETE RESTRICT"

    override fun toString(): String {
        return super.sqlDefinition + " - " + sqlReference
    }
}

class DBCheck(private val condition: String) : SQLType {

    override val sqlName: String
        get() = ""

    override val sqlDefinition: String
        get() = "CHECK ($condition)"
}

class DBPrimaryKeys(private vararg val columns: String) : SQLType {

    override val sqlName: String
        get() = ""

    override val sqlDefinition: String
        get() = "PRIMARY KEY (${columns.joinToString(", ")})"
}
