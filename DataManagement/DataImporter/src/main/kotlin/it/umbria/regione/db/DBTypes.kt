package it.umbria.regione.db

import it.umbria.regione.model.*
import java.sql.PreparedStatement

private fun Any?.toNumber(): Number {
    return when (this) {
        is Number -> this
        is String -> this.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $this")
        else -> throw IllegalArgumentException("Invalid number: $this")
    }
}

object DBInt : DBType("INT") {
    override fun setValue(p: PreparedStatement, index: Int, value: Any?) {
        if (value == null || value == "")
            p.setNull(index, java.sql.Types.INTEGER)
        else {
            p.setInt(index, value.toNumber().toInt())
        }
    }
}

object DBDouble : DBType("DOUBLE") {
    override fun setValue(p: PreparedStatement, index: Int, value: Any?) {
        if (value == null || value == "" || value == "N/A")
            p.setNull(index, java.sql.Types.DOUBLE)
        else
            p.setDouble(index, value.toNumber().toDouble())
    }
}

class DBChar(size: Int) : DBType("CHAR($size)") {
    override fun setValue(p: PreparedStatement, index: Int, value: Any?) {
        if (value == null)
            p.setNull(index, java.sql.Types.CHAR)
        else
            p.setString(index, value.toString())
    }
}

class DBVarChar(size: Int = 255) : DBType("VARCHAR($size)") {
    override fun setValue(p: PreparedStatement, index: Int, value: Any?) {
        if (value == null)
            p.setNull(index, java.sql.Types.VARCHAR)
        else
            p.setString(index, value.toString())
    }
}

val DBVarchar = DBVarChar(255)

object DBText : DBType("TEXT") {
    override fun setValue(p: PreparedStatement, index: Int, value: Any?) {
        if (value == null)
            p.setNull(index, java.sql.Types.VARCHAR)
        else
            p.setString(index, value.toString())
    }
}

object DBDate : DBType("DATE") {
    override fun setValue(p: PreparedStatement, index: Int, value: Any?) {
        if (value == null)
            p.setNull(index, java.sql.Types.DATE)
        else
            p.setDate(
                index, when (value) {
                    is java.sql.Date -> value
                    is java.util.Date -> java.sql.Date(value.time)
                    is ModelDate -> value.toSqlDate()
                    is String -> value.toString().toModelDate().toSqlDate()
                    else -> throw IllegalArgumentException("Invalid date type: ${value.javaClass}")
                }
            )
    }
}

object DBTime : DBType("TIME") {
    override fun setValue(p: PreparedStatement, index: Int, value: Any?) {
        if (value == null)
            p.setNull(index, java.sql.Types.TIME)
        else
            p.setTime(
                index, when (value) {
                    is java.sql.Time -> value
                    is java.util.Date -> java.sql.Time(value.time)
                    is ModelTime -> value.toSqlTime()
                    else -> throw IllegalArgumentException("Invalid time type: ${value.javaClass}")
                }
            )
    }
}

object DBTimestamp : DBType("TIMESTAMP") {
    override fun setValue(p: PreparedStatement, index: Int, value: Any?) {
        if (value == null)
            p.setNull(index, java.sql.Types.TIMESTAMP)
        else
            p.setTimestamp(
                index, when (value) {
                    is java.sql.Timestamp -> value
                    is java.util.Date -> java.sql.Timestamp(value.time)
                    is ModelDateTime -> value.toTimeStamp()
                    else -> throw IllegalArgumentException("Invalid timestamp type: ${value.javaClass}")
                }
            )
    }
}

class DBEnum(private vararg val values: String) : DBType("ENUM") {
    override val sqlType: String
        get() = "ENUM(${values.joinToString(separator = ", ") { """"$it"""" }})"

    override fun setValue(p: PreparedStatement, index: Int, value: Any?) {
        if (value == null)
            p.setNull(index, java.sql.Types.VARCHAR)
        else
            p.setString(index, value as String)
    }
}

object DBBoolean : DBType("BOOLEAN") {
    override fun setValue(p: PreparedStatement, index: Int, value: Any?) {
        if (value == null)
            p.setNull(index, java.sql.Types.BOOLEAN)
        else {
            val bool = when (value) {
                is Boolean -> value
                is Number -> value.toInt() != 0
                is String -> setOf("SI", "TRUE", "VERO").contains(value.uppercase())
                else -> {
                    throw IllegalArgumentException("Invalid boolean value: $value")
                }
            }
            p.setBoolean(index, bool)
        }
    }
}
