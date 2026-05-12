package it.umbria.regione.pnrr

import it.umbria.regione.config.LocalDataImporterParameters
import it.umbria.regione.pnrr.io.ImportPNRRFromExcel
import it.umbria.regione.model.toModelDate
import it.umbria.regione.pnrr.db.PNRRDatabase
import java.io.File

fun main(args: Array<String>) {
    val p = LocalDataImporterParameters().apply {
        inputFile = File(defaultInputDir, "$defaultImportDate/$defaultFileName").absolutePath
        importDate = defaultImportDate.toModelDate()
    }
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
        val now = System.currentTimeMillis()
        ImportPNRRFromExcel(db).importExcel(p)
        val end = System.currentTimeMillis()
        // elapsed time formatted as MM:SS
        println("Elapsed time: ${String.format("%02d:%02d", (end - now) / 60000, (end - now) % 60000 / 1000)}")
    }
}
