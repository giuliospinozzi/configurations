package it.umbria.regione.io

import it.umbria.regione.model.ModelDate
import it.umbria.regione.model.ModelTime
import it.umbria.regione.model.toModelDate
import it.umbria.regione.model.toModelTime
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType

fun Cell.string(): String? {
    return when (cellType) {
        CellType.NUMERIC -> numericCellValue.toString()
        else -> stringCellValue.trim().ifBlank { null }
    }
}

fun Cell.date(): ModelDate? {
    return when (cellType) {
        CellType.NUMERIC -> dateCellValue.toModelDate()
        CellType.STRING -> string().takeUnless { it == "#" }?.toModelDate()
        else -> null
    }
}

fun Cell.boolean(): Boolean? {
    return when (cellType) {
        CellType.BOOLEAN -> booleanCellValue
        CellType.STRING -> string() == "OK"
        else -> null
    }
}

fun Cell.time(): ModelTime? {
    return string()?.trim()?.toModelTime()
}

fun String.time(prefix: String = ""): ModelTime? {
    return if (startsWith(prefix)) {
        substring(prefix.length).trim().toModelTime()
    } else {
        null
    }
}

fun Cell.double(): Double {
    try {
        return when (cellType) {
            CellType.NUMERIC -> numericCellValue
            CellType.STRING -> stringCellValue.takeUnless { it.isNullOrEmpty() }?.toDouble() ?: 0.0
            CellType.BLANK -> 0.0
            else -> throw IllegalStateException("Cell $address has type $cellType")
        }
    } catch (e: IllegalStateException) {
        println("Error in converting $stringCellValue to double")
        throw e
    }
}

fun Cell.int(): Int {
    return double().toInt()
}
