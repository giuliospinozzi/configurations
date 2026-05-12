package it.regione.umbria.test

import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import it.umbria.regione.config.DatabaseParameters
import it.umbria.regione.pnrr.db.PNRRDatabase
import java.io.File

fun main(args: Array<String>) {
    val cupIndex = 0
    val dir = System.getProperty("user.home") + "/Desktop/"
    val file = File("$dir/OPENCUP.csv")
    val p = DatabaseParameters()
    p.mergeArguments(File("config_local.properties"), args)
    val db = PNRRDatabase(p)
    val out = CSVWriter(File("$dir/output.csv").writer())
    CSVReader(file.reader()).use { reader ->
        reader.readAll().forEachIndexed { index, strings ->
            if (index == 0) {
                val newArray = strings.insertAt("Last Date", cupIndex + 1)
                out.writeNext(newArray)
                return@forEachIndexed
            } else {
                val cup = strings[cupIndex]
                val lastDate = db.lastDateOf(cup)
                val newArray = strings.insertAt(lastDate?.toString() ?: "<not found>", cupIndex + 1)
                out.writeNext(newArray)
            }
        }
    }
    out.flush()
    out.close()
    db.close()
}

fun Array<String>.insertAt(value: String, index: Int): Array<String> {
    val list = this.toMutableList()
    list.add(index, value)
    return list.toTypedArray()
}
