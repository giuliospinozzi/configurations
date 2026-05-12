package it.umbria.regione.config

import it.umbria.regione.model.ModelDate
import org.kohsuke.args4j.Option
import java.io.InputStream

abstract class DataImporterParameters : DatabaseParameters() {

    @Option(name = "--dropDatabase", usage = "Drop database before importing")
    var dropDatabase: Boolean = false

    @Option(name = "--importDate", metaVar = "DATE", usage = "Import date")
    var importDate: ModelDate = ModelDate()

    abstract fun openInputStream(): InputStream
}
