package it.umbria.regione.pnrr

import it.umbria.regione.config.LocalDataImporterParameters
import it.umbria.regione.pnrr.io.ImportPNRRFromExcel
import it.umbria.regione.pnrr.db.PNRRDatabase
import java.io.File

fun main(args: Array<String>) {
    val p = LocalDataImporterParameters()
    val config = args.indexOf("--config").let {
        if (it >= 0)
            args[it + 1]
        else
            "config.properties"
    }
    val file = File(config)
    if (file.exists())
        p.mergeArguments(file, args)
    else
        p.readFromArgs(args)
    PNRRDatabase(p).use { db ->
        println("Importing data from ${File(p.inputFile!!).absoluteFile}, overwrite: ${p.dropDatabase}")
        ImportPNRRFromExcel(db).importExcel(p)
    }
}
