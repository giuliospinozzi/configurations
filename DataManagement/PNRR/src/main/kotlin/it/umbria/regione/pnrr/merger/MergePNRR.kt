package it.umbria.regione.pnrr.merger

import it.umbria.regione.pnrr.*
import java.io.File

fun main() {
    val merger = RegisMerger(File(defaultInputDir), defaultImportDate)
    val output = File(defaultInputDir, "$defaultImportDate/$defaultFileName")
    merger.mergeAndExportToFile(output)
}
