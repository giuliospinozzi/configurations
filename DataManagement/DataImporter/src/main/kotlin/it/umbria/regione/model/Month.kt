package it.umbria.regione.model

data class Month(val month: Int, val year: Int) : Comparable<Month> {

    override fun compareTo(other: Month): Int {
        var c = year.compareTo(other.year)
        if (c == 0)
            c = month.compareTo(other.month)
        return c
    }

    override fun toString(): String {
        return "${month.pad()}/$year"
    }
}
