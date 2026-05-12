package it.umbria.regione.cdc

import it.umbria.regione.cdc.db.CdCDatabase
import it.umbria.regione.cdc.io.ImportCdCFromExcel
import it.umbria.regione.config.LocalDataImporterParameters
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
    CdCDatabase(p).use { db ->
        println("Importing data from ${defaultInputDir}, overwrite: ${p.dropDatabase}")
        ImportCdCFromExcel(db).importFiles(File(defaultInputDir), p)
    }
}
