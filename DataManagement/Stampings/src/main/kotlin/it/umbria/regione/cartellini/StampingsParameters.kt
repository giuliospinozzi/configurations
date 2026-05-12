package it.umbria.regione.cartellini

import it.umbria.regione.config.LocalDataImporterParameters
import it.umbria.regione.model.ModelDate
import it.umbria.regione.model.toSqlDate
import org.kohsuke.args4j.Option
import org.plib.joinToStringIfAny

object StampingsParameters : LocalDataImporterParameters() {

    init {
        dbName = "stampings"
    }

    @Option(name = "--byName", depends = ["--outputFile"], metaVar = "NAME", usage = "Filter by name")
    var byName: String? = null

    @Option(name = "--fromDate", depends = ["--outputFile"], metaVar = "DATE", usage = "Filter from date")
    var fromDate: ModelDate? = null

    @Option(name = "--toDate", depends = ["--outputFile"], metaVar = "DATE", usage = "Filter to date")
    var toDate: ModelDate? = null

    @Option(name = "--outputFile", metaVar = "FILE", usage = "Output file")
    var outputFile: String? = null

    fun toCommonWhere(): String {
        return listOfNotNull(
            byName?.let { "COGNOME_NOME LIKE '%$it%'" },
            fromDate?.let { "data >= '${it.toSqlDate()}'" },
            toDate?.let { "data <= '${it.toSqlDate()}'" }
        ).joinToStringIfAny(prefix = "WHERE ", separator = " AND ")
    }

    override fun toString(): String {
        return """${super.toString()}
            |   byName='$byName'
            |   fromDate=$fromDate
            |   toDate=$toDate""".trimMargin()
    }
}

