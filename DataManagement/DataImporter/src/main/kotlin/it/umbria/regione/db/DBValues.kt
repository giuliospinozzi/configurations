package it.umbria.regione.db

class DBValues(val table: DBTable) {

    private val values = mutableMapOf<DBColumn, Any?>()

    operator fun get(column: DBColumn): Any? = values[column]

    operator fun get(column: String): Any? = this[table[column]]

    operator fun set(column: DBColumn, value: Any?) {
        values[column] = value
    }

    operator fun set(column: String, value: Any?) {
        this[table[column]] = value
    }

    fun set(vararg pairs: Pair<String, Any?>): DBValues {
        pairs.forEach { (column, value) -> this[column] = value }
        return this
    }

    override fun toString(): String {
        return """Values(${table.columns.joinToString(", ") { "${it.name}=${this[it] ?: "<null>"}" }}"""
    }
}
