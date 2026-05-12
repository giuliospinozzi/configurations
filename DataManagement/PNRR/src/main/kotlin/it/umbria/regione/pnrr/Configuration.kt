package it.umbria.regione.pnrr

import com.opencsv.CSVIterator
import com.opencsv.CSVReader
import it.umbria.regione.pnrr.db.PNRRDatabase

const val defaultInputDir =
    "/Users/gspinozzi/Workspace/ReGiS"
var defaultImportDate = "20250414"

val defaultFileName: String
    get() = "RAL ${defaultImportDate}.xlsx"
const val skipPNC = true

typealias CUP = String
typealias CIP = String

val comuni: Map<String, String> by lazy {
    val map = mutableMapOf<String, String>()
    val inp = PNRRDatabase::class.java.getResourceAsStream("/Comuni.csv")!!
    inp.reader().use { fr ->
        CSVReader(fr).use { r ->
            r.readNext()
            CSVIterator(r).forEach { v ->
                map[v[0]] = v[1]
            }
        }
    }
    map
}
