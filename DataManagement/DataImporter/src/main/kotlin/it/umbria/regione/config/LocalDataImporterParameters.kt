package it.umbria.regione.config

import org.kohsuke.args4j.Option
import java.io.File

open class LocalDataImporterParameters : DataImporterParameters() {

    @Option(name = "--inputFile", metaVar = "FILE", usage = "Input file")
    var inputFile: String? = null

    override fun openInputStream() = File(inputFile!!).inputStream()
}
