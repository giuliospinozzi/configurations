package it.umbria.regione.pnrr.io

import it.umbria.regione.config.DataImporterParameters
import it.umbria.regione.db.DBValues
import it.umbria.regione.io.*
import it.umbria.regione.model.HeaderIndex
import it.umbria.regione.pnrr.CIP
import it.umbria.regione.pnrr.comuni
import it.umbria.regione.pnrr.db.*
import org.apache.poi.ss.usermodel.Row
import java.text.ParseException

class ImportPNRRFromExcel(db: PNRRDatabase) : ExcelToDatabaseImporter(db) {

    private var nFasi: Int = 0
    private lateinit var header: HeaderIndex

    override fun prepareHeader(sheetNumber: Int, row: Row) {
        if (sheetNumber == 0) {
            nFasi = (0 until row.lastCellNum).map {
                row.getCell(it).stringCellValue
            }.filter {
                it.startsWith("Fase procedurale")
            }.size
            header = createPncHeader(nFasi)
            header.checkHeader(row)
        } else
            header = pncPDAHeader
    }

    override fun prepareDatabase(p: DataImporterParameters) {
        super.prepareDatabase(p)
        (db as PNRRDatabase).createDefaultMissions()
    }

    fun importExcel(p: DataImporterParameters) {
        prepareDatabase(p)
        val cipsId = mutableMapOf<CIP, Int>()
        val pncDb = db as PNRRDatabase
        pncDb.startTransaction()
        readWorkbook(p) { index, row ->
            val prId = tableProgetti.createValues(index) { values ->
                values.setFromRow(row, cipLabel) { it.string() }
                values.setFromRow(row, "Descrizione progetto") { it.string() }
                values.setFromRow(row, cupLabel) { it.string() }
                values.setFromRow(row, "Natura CUP") { it.string() }
                values.setFromRow(row, "CLP") { it.string() }
                values.setFromRow(row, "Stato avanzamento") { it.string() }

                values["soggettoAttuatore"] = findSoggetto(row, index, "Soggetto attuatore", pncDb)

                values.setFromRow(row, "Data inizio prevista") { it.date() }
                values.setFromRow(row, "Data fine prevista") { it.date() }
                values.setFromRow(row, "Data inizio effettiva") { it.date() }
                values.setFromRow(row, "Data fine effettiva") { it.date() }

                values.setFromRow(row, "Tipo investimento") { it.string() }
                values.setFromRow(row, "Tipo fondo") { it.string() }

                values["finanziamentoPNRR"] = insertRisorse(row, index, pncDb, "PNRR")
                values["finanziamentoPNC"] = insertRisorse(row, index, pncDb, "PNC")
                values["finanziamentoPNCSISMA2016"] = insertRisorse(row, index, pncDb, "PNC SISMA 2016")

                values.setFromRow(row, "Settore") { it.string() }
                values.setFromRow(row, "Ministero titolare") { it.string() }
                values.setFromRow(row, "Struttura competente attuatore") { it.string() }
                values["enteBeneficiario"] = findSoggetto(row, index, "Ente beneficiario", pncDb)
                values["soggettoRealizzatore"] = findSoggetto(row, index, "Soggetto realizzatore", pncDb)
                values["altriSoggettiRealizzatori"] = findSoggetto(row, index, "Altri soggetti", pncDb)
                values.setFromRow(row, "Programma del piano") { it.string() }
                values.setFromRow(row, "Programma cofinanziato") { it.string() }
                values.setBoolean(row, "Esito prevalidazione")
                values.setFromRow(row, "Data ultima prevalidazione") { it.date() }
                values.setBoolean(row, "Esito validazione")
                values.setFromRow(row, "Data ultima validazione") { it.date() }
                val id = pncDb.insertDataWithKey(tableProgetti, values, p.importDate)
                cipsId[values[cipLabel.toDatabaseLabel()].toString()] = id
                id
            }
            val mKeys = findInterventi(row, pncDb)
            mKeys.forEach { mKey ->
                tableProgettiInterventi.createValues(index) { values ->
                    values["progetto"] = prId
                    values["riferimentoIntervento"] = mKey.key
                    values["tipoRiferimentoIntervento"] = mKey.reference.name
                    pncDb.insertData(tableProgettiInterventi, values)
                    0
                }
            }
            pncDb.prepareInsert(tableProgettiInterventi).executeBatch()

            var fase = 0
            if (nFasi > 0)
                for (i in fileHeader["Fase procedurale 1"]..fileHeader["Data fine effettiva fase $nFasi"] step 6) {
                    fase++
                    if (row.getCell(i + 2).dateCellValue?.toString().isNullOrBlank())
                        break
                    tableFasi.createValues(index) { values ->
                        values["progetto"] = prId
                        values["numeroFase"] = fase
                        values.setFromRow(row, i, "codiceFase") { it.string() }
                        values.setFromRow(row, i + 1, "descrizione") { it.string() }
                        values.setFromRow(row, i + 2, "dataInizioPrevista") { it.date() }
                        values.setFromRow(row, i + 3, "dataInizioEffettiva") { it.date() }
                        values.setFromRow(row, i + 4, "dataFinePrevista") { it.date() }
                        values.setFromRow(row, i + 5, "dataFineEffettiva") {
                            try {
                                it.date()
                            } catch (p: ParseException) {
                                // TERMINE NON RISPETTATO
                                null
                            }
                        }
                        pncDb.insertDataWithKey(tableFasi, values)
                    }
                }
            var mile = 0
            if (nFasi > 0)
                for (i in fileHeader["Milestone 1"]..fileHeader["Data milestone $nFasi"] step 2) {
                    mile++
                    if (row.getCell(i)?.stringCellValue.isNullOrBlank())
                        break
                    tableMilestone.createValues(index) { values ->
                        values["progetto"] = prId
                        values["numeroMilestone"] = mile
                        values.setFromRow(row, i, "descrizione") { it.string() }
                        values.setFromRow(row, i + 1, "data") { it.date() }
                        values.setFromRow(row, i + 1, "europea") { it.boolean() }
                        pncDb.insertDataWithKey(tableMilestone, values)
                    }
                }
        }
        readWorkbook(p, 1) { index, row ->
            tablePDA.createValues(index) { values ->
                val cip = row.getCell(cipLabel).string()
                if (!cipsId.containsKey(cip)) {
                    println("CIP $cip not found")
                    -1
                } else {
                    val codPda = row.getCell("Codice interno PDA").string()
                    if (codPda != "#") {
                        values["progetto"] = cipsId[cip]
                        values["codiceInternoPDA"] = codPda
                        values.setFromRow(row, "Data pubblicazione PDA") { it.date() }
                        values.setFromRow(row, "Data aggiudicazione definitiva PDA") { it.date() }
                        values.setFromRow(row, "Tipo PDA") { it.string() }
                        values.setFromRow(row, "CIG") { it.string() }
                        values.setFromRow(row, "Motivo assenza CIG") { it.string() }
                        pncDb.insertData(tablePDA, values)
                        0
                    } else
                        -1
                }
            }
        }
        pncDb.prepareInsert(tablePDA).executeBatch()
        pncDb.commit()
    }

    private fun DBValues.setBoolean(row: Row, name: String) {
        setFromRow(row, name) {
            val s = it.string()
            when {
                s == "OK" -> true
                s.isNullOrBlank() -> null
                else -> false
            }
        }
    }

    private fun findSoggetto(row: Row, index: Int, name: String, pncDb: PNRRDatabase): Int? {
        val soggetto = row.getCell(name).string()
        return soggetto?.let { s ->
            pncDb.findId(tableSoggetti, "nome", s) {
                createValues(index) { values ->
                    values["nome"] = soggetto
                    values["provincia"] = comuni[soggetto]
                    pncDb.insertDataWithKey(this, values)
                }
            }
        }
    }

    private fun findInterventi(row: Row, pncDb: PNRRDatabase): List<InterventoKey> {
        val interventi = mutableListOf<InterventoKey>()
        val misura = row.getCell("Misura").string()
        val submisura = row.getCell("Submisura").string()
        if (misura != null || submisura != null) {
            interventi.add(
                pncDb.collectIntervento(
                    tables = arrayOf(tablePNRRMissioni, tablePNRRComponenti, tablePNRRMisure, tablePNRRSubmisure),
                    values = arrayOf(
                        (row.getCell("Missione").string() ?: findMissione(misura, submisura)) to null,
                        row.getCell("Componente").string() to row.getCell("Descrizione componente").string(),
                        misura to row.getCell("Descrizione misura").string(),
                        submisura to row.getCell("Descrizione submisura").string()
                    )
                )
            )
        }
        val pnc = row.getCell("PNC Macro misura").string()
        if (pnc != null) {
            interventi.add(
                pncDb.collectIntervento(
                    tables = arrayOf(tablePNCMacroMisure, tablePNCSubmisure, tablePNCLineeIntervento),
                    values = arrayOf(
                        pnc to row.getCell("PNC Descrizione macro misura").string(),
                        row.getCell("PNC Submisura").string() to row.getCell("PNC Descrizione submisura").string(),
                        row.getCell("PNC Linea intervento").string() to row.getCell("PNC Descrizione linea intervento")
                            .string()
                    )
                )
            )
        }
        val pncsisma = row.getCell("PNC SISMA 2016 Macro misura").string()
        if (pncsisma != null) {
            interventi.add(
                pncDb.collectIntervento(
                    tables = arrayOf(tablePNCSISMAMacroMisure, tablePNCSISMASubmisure, tablePNCSISMALineeIntervento),
                    values = arrayOf(
                        pncsisma to row.getCell("PNC SISMA 2016 Descrizione macro misura").string(),
                        row.getCell("PNC SISMA 2016 Submisura")
                            .string() to row.getCell("PNC SISMA 2016 Descrizione submisura")
                            .string(),
                        row.getCell("PNC SISMA 2016 Linea intervento")
                            .string() to row.getCell("PNC SISMA 2016 Descrizione linea intervento")
                            .string()
                    )
                )
            )
        }
        return interventi
    }

    private fun findMissione(misura: String?, submisura: String?): String {
        val s = misura ?: submisura!!
        return s.substring(0, 2)
    }

    private fun insertRisorse(row: Row, index: Int, pncDb: PNRRDatabase, type: String): Int? {
        return tableFinanziamenti.createValues(index) { values ->
            values["tipoFinanziamento"] = type
            var amount = 0.0
            values.setFromRow(row, "$type - Risorse finanziarie", "risorseFinanziarie") {
                it.double().also { d -> amount += d }
            }
            values.setFromRow(row, "$type - Impegni totali", "impegniTotali") { it.double().also { d -> amount += d } }
            values.setFromRow(row, "$type - Pagamenti totali", "pagamentiTotali") {
                it.double().also { d -> amount += d }
            }
            values.setFromRow(row, "$type - Importo da realizzare", "importoDaRealizzare") {
                it.double().also { d -> amount += d }
            }
            values.setFromRow(row, "$type - Importo realizzato nell'anno", "importoRealizzatoAnno") {
                it.double().also { d -> amount += d }
            }
            values.setFromRow(row, "$type - Finanziamento totale", "finanziamentoTotale") {
                it.double().also { d -> amount += d }
            }
            values.setFromRow(row, "$type - Finanziamento", "finanziamento") { it.double().also { d -> amount += d } }
            if (type == "PNRR") {
                values.setFromRow(row, "Importo pagamento validato RGS") { it.double().also { d -> amount += d } }
                values.setFromRow(row, "Finanziamento Stato FOI") { it.double().also { d -> amount += d } }
            }
            values.setFromRow(row, "Percentuale localizzazione") { if (it.string().isNullOrBlank()) null else it.double() }
            if (amount > 0)
                pncDb.insertDataWithKey(tableFinanziamenti, values)
            else
                -1
        }.let { if (it > 0) it else null }
    }

    override val fileHeader: HeaderIndex
        get() = header
}
