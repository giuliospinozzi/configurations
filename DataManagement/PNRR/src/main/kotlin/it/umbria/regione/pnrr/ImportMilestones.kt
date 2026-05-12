package it.umbria.regione.pnrr

import it.umbria.regione.config.LocalDataImporterParameters
import it.umbria.regione.db.DBTable
import it.umbria.regione.db.XLSDatabase
import it.umbria.regione.pnrr.io.ImportPNRRFromExcel
import it.umbria.regione.model.toModelDate
import it.umbria.regione.pnrr.db.PNRRDatabase
import it.umbria.regione.pnrr.db.tableItaliaDomaniMilestones
import it.umbria.regione.pnrr.io.ImportMilestones
import java.io.File

fun main(args: Array<String>) {
    val p = LocalDataImporterParameters().apply {
        inputFile = File(defaultInputDir, "PNRR_Milestone.csv").absolutePath
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
    object : XLSDatabase(p) {
        override val tables: Array<out DBTable>
            get() = arrayOf(tableItaliaDomaniMilestones)
    }.use { db ->
        val now = System.currentTimeMillis()
        ImportMilestones(db).importCSVFile(p)
        val end = System.currentTimeMillis()
        // elapsed time formatted as MM:SS
        println("Elapsed time: ${String.format("%02d:%02d", (end - now) / 60000, (end - now) % 60000 / 1000)}")
    }
}
