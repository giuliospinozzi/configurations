package it.umbria.regione.cartellini

import it.umbria.regione.cartellini.db.StampingDatabase
import it.umbria.regione.cartellini.io.ImportOriginalExcelToDatabase
import it.umbria.regione.cartellini.io.StampingsExporter
import it.umbria.regione.cartellini.io.StampingsImporter
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val p = StampingsParameters
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
    if (p.help) {
        p.printUsage()
        exitProcess(0)
    }
    p.printConfig()
    val db = StampingDatabase(p)
    p.inputFile?.let { i ->
        val input = File(i)
        println("Importing data from ${input.absoluteFile}, anonymous=${anonymous}, overwrite=${p.dropDatabase}")
        ImportOriginalExcelToDatabase(db).importFile(p)
    }
    p.outputFile?.let { o ->
        println("Getting data from database")
        val dm = StampingsImporter().importFromDatabase(db)
        val out = File(o)
        println("Exporting data to ${out.absoluteFile}")
        StampingsExporter(dm).exportStampings(out)
    }
    db.close()
}

