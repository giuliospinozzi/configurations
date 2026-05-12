package it.umbria.regione.pnrr.cloud

import it.umbria.regione.config.DataImporterParameters
import it.umbria.regione.model.ModelDate
import java.io.InputStream
import java.util.Properties

class CloudParameters(private val input: InputStream) : DataImporterParameters() {

    init {
        val prop = Properties().apply {
            CloudParameters::class.java.getResourceAsStream("/conf.properties").use { load(it) }
        }
        this.dbUrl = prop.getProperty("dbUrl")
        this.dbPort = prop.getProperty("dbPort").toInt()
        this.dbName = prop.getProperty("dbName")
        this.dbUser = prop.getProperty("dbUser")
        this.dbPassword = prop.getProperty("dbPassword")
        this.dropDatabase = prop.getProperty("dropDatabase").toBoolean()
        this.importDate = ModelDate()
    }

    override fun openInputStream(): InputStream {
        return input
    }
}
