package it.umbria.regione.pnrr

import it.umbria.regione.config.LocalDataImporterParameters
import it.umbria.regione.model.toModelDate
import it.umbria.regione.pnrr.db.PNRRDatabase
import it.umbria.regione.pnrr.io.ImportPNRRFromExcel
import java.io.File

fun main(args: Array<String>) {
    val p = LocalDataImporterParameters()
    val config = args.indexOf("--config").let {
        if (it >= 0)
            args[it + 1]
        else
            "config.properties"
    }
    p.mergeArguments(File(config), args)
    PNRRDatabase(p).use { db ->
        p.dropDatabase = true
        val im = ImportPNRRFromExcel(db)
        for (y in arrayOf("20240605", "20240716", "20240805")) {
            p.inputFile = "$defaultInputDir/$y/RAL $y.xlsx"
            p.importDate = y.toModelDate()
            println("Importing data from ${p.inputFile}, overwrite: ${p.dropDatabase}")
            im.importExcel(p)
            p.dropDatabase = false
        }
    }
}
