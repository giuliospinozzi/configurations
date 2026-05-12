package it.umbria.regione.db

import it.umbria.regione.config.DatabaseParameters
import it.umbria.regione.model.ModelDate
import org.plibrary.dao.ConnectionDriver
import java.sql.Connection
import java.sql.PreparedStatement
import java.util.*

abstract class XLSDatabase(private val dbParams: DatabaseParameters) : AutoCloseable {

    protected val conn: Connection
    protected abstract val tables: Array<out DBTable>
    private val preparedInsert = mutableMapOf<DBTable, PreparedStatement>()

    init {
        val driver = ConnectionDriver.createMySQL().apply {
            url = dbParams.dbUrl
            port = dbParams.dbPort
            databaseName = dbParams.dbName
        }
        conn = driver.openConnection(dbParams.dbUser, dbParams.dbPassword)
    }

    fun checkTableExists() {
        tables.forEach {
            createTable(it)
        }
    }

    private fun createTable(table: DBTable) {
        conn.createStatement().use {
            it.executeUpdate(table.sqlDefinition)
        }
    }

    fun prepareInsert(table: DBTable): PreparedStatement {
        return preparedInsert.computeIfAbsent(table) { t ->
            val cols = t.columns.filterNot { it.hasAutoincrement }
            val sql = "INSERT INTO ${t.name} (" +
                    cols.joinToString(", ") { it.name } +
                    ") VALUES (" +
                    cols.joinToString(", ") { "?" } +
                    ");"
            conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        }
    }

    fun findId(table: DBTable, field: String, value: String, orCreate: DBTable.() -> Int = { 0 }): Int {
        return conn.prepareStatement("SELECT ${ID.name} FROM ${table.name} WHERE $field = ?;").use {
            it.setString(1, value)
            it.executeQuery().use { r ->
                if (r.next()) r.getInt(ID.name) else table.orCreate()
            }
        }
    }

    fun existsOrCreate(table: DBTable, field: String, value: String, orCreate: DBTable.() -> Unit = {}) {
        conn.prepareStatement("SELECT $field FROM ${table.name} WHERE $field = ?;").use {
            it.setString(1, value)
            it.executeQuery().use { r ->
                if (!r.next())
                    table.orCreate()
            }
        }
    }

    fun insertDataWithKey(table: DBTable, vararg values: Pair<String, Any?>): Int {
        return insertDataWithKey(table, DBValues(table).set(*values))
    }

    open fun insertDataWithKey(table: DBTable, values: DBValues, importDate: ModelDate? = null): Int {
        val cols = values.table.columns.filterNot { it.hasAutoincrement }
        val ins = prepareInsert(table)
        importDate?.let { values[IMPORT_DATE] = it }
        cols.forEachIndexed { index, dbColumn ->
            dbColumn.type.setValue(ins, index + 1, values[dbColumn])
        }
        try {
            ins.executeUpdate()
        } catch (e: Exception) {
            println(ins.toString())
            throw e
        }
        val hasGeneratedKey = values.table.columns.any { it.hasAutoincrement }
        return if (hasGeneratedKey) ins.generatedKeys.use {
            if (it.next())
                it.getInt(1)
            else
                throw Exception("No generated keys")
        } else
            0
    }

    fun insertData(table: DBTable, vararg values: Pair<String, Any?>): PreparedStatement {
        return insertData(table, DBValues(table).set(*values))
    }

    open fun insertData(table: DBTable, values: DBValues, importDate: ModelDate? = null): PreparedStatement {
        val cols = values.table.columns.filterNot { it.hasAutoincrement }
        val ins = prepareInsert(table)
        importDate?.let { values[IMPORT_DATE] = it }
        cols.forEachIndexed { index, dbColumn ->
            dbColumn.type.setValue(ins, index + 1, values[dbColumn])
        }
        ins.addBatch()
        return ins
    }

    private fun deleteTable(table: DBTable) {
        conn.createStatement().use { st ->
            st.executeUpdate("DELETE FROM ${table.name};")
            st.executeUpdate("ALTER TABLE ${table.name} AUTO_INCREMENT = 1")
        }
    }

    fun deleteTables() {
        tables.forEach { t ->
            deleteTable(t)
        }
    }

    private fun dropTable(table: DBTable) {
        conn.createStatement().use {
            it.executeUpdate("DROP TABLE IF EXISTS ${table.name};")
        }
    }

    fun dropTables() {
        tables.reversedArray().forEach { t ->
            dropTable(t)
        }
    }

    override fun close() {
        preparedInsert.values.forEach { it.close() }
        conn.close()
    }

    fun startTransaction() {
        conn.autoCommit = false
    }

    fun commit() {
        conn.commit()
        conn.autoCommit = true
    }

    protected fun Date.toSql() = java.sql.Date(time)
    protected fun Date.toTimeSql() = java.sql.Time(time)
}
