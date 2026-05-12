package it.umbria.regione.model

import org.apache.poi.ss.usermodel.Row

class HeaderIndex(vararg pairs: Pair<String, Int>) {

    private val header: Map<String, Int> = mapOf(*pairs)

    operator fun get(key: String): Int {
        return header[key] ?: throw IllegalArgumentException("Header $key not found")
    }

    val values: Iterable<String>
        get() = header.entries.sortedBy { it.value }.map { it.key }

    fun toCSV(): String {
        return values.joinToString(",")
    }

    override fun toString(): String {
        return header.entries.joinToString(",")
    }

    fun newValues(): HeaderValues {
        return HeaderValues(this)
    }

    // Check if every label in the header is almost similar to the one in the row
    fun checkHeader(row: Row) {
        row.forEachIndexed { index, cell ->
            val myLabel = header.keys.find { header[it] == index }!!
            val xlsLabel = cell.stringCellValue.lowercase().split(" ")
            val notIn = myLabel.lowercase().split(" ").filter { !xlsLabel.contains(it) }
            if (notIn.isNotEmpty())
                throw IllegalArgumentException("Header at index $index should be '$myLabel' (instead of '${cell.stringCellValue}', missing $notIn)")
        }
    }
}

class HeaderValues internal constructor(private val index: HeaderIndex) {

    private val values = mutableMapOf<String, String?>()

    operator fun get(label: String): String? {
        if (!index.values.contains(label))
            throw IllegalArgumentException("Label $label not found")
        return values[label]
    }

    operator fun set(label: String, value: String?) {
        if (!index.values.contains(label))
            throw IllegalArgumentException("Label $label not found")
        values[label] = value
    }

    fun toCSV(): String {
        return index.values.joinToString(",") { this[it] ?: "" }
    }

    override fun toString(): String {
        return values.entries.joinToString(",")
    }

    fun setFrom(other: HeaderValues) {
        index.values.forEach { l -> this[l] = other[l] }
    }
}
