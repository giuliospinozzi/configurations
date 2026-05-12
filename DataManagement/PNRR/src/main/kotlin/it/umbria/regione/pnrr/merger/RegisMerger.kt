package it.umbria.regione.pnrr.merger

import it.umbria.regione.model.HeaderIndex
import it.umbria.regione.model.HeaderValues
import it.umbria.regione.pnrr.CIP
import it.umbria.regione.pnrr.io.*
import it.umbria.regione.pnrr.io.createPncHeader
import it.umbria.regione.pnrr.io.headerCupCosti
import it.umbria.regione.pnrr.io.headerCupFasi
import it.umbria.regione.pnrr.io.headerCupMisure
import it.umbria.regione.pnrr.io.headerCupPDA
import it.umbria.regione.pnrr.io.pncPDAHeader
import it.umbria.regione.pnrr.skipPNC
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.plibrary.util.ProgressCalculator
import java.io.File
import java.text.SimpleDateFormat

class RegisMerger(
    private val cupCosti: File,
    private val cupFasi: File,
    private val cupMisure: File,
    private val cupPDA: File
) {

    constructor(dir: File, date: String) : this(
        File(dir, "$date/RAL $date CUP e Costi.xlsx"),
        File(dir, "$date/RAL $date CUP e Fasi.xlsx"),
        File(dir, "$date/RAL $date CIP e Misure.xlsx"),
        File(dir, "$date/RAL $date CUP e PDA.xlsx")
    )

    private lateinit var pncHeader: HeaderIndex

    private val dateFormatterEng = SimpleDateFormat("yyyyMMdd")
    private val dateFormatterIta = SimpleDateFormat("dd/MM/yyyy")
    private lateinit var dateStyle: CellStyle
    private val calc = ProgressCalculator()

    fun mergeAndExportToFile(output: File) {
        val costi = readCosti(cupCosti)
        calc.maximum = costi.size.toLong()
        val fasi = readFasi(cupFasi)
        val misure = readMisure(cupMisure)
        val pda = (if (cupPDA.exists()) readPDA(cupPDA) else emptyList()).toMutableList()
        val values = mutableListOf<HeaderValues>()
        pncHeader = createPncHeader(fasi.values.maxOf { it.size })
        costi.keys.distinct().forEach { cip ->
            calc.doStep()
            calc.printGraphicOutput("Merging projects")
            val tipoFondo = misure[cip]!!.tipoFondo
            if (skipPNC && tipoFondo == "PNC") {
                pda.removeIf { it.cip == cip }
                return@forEach
            }
            val v = pncHeader.newValues()
            mergeCosto(v, costi[cip]!!, tipoFondo)
            fasi[cip]?.forEachIndexed { index, fase ->
                mergeFase(v, fase, index + 1)
            }
            mergeMisura(v, misure[cip]!!)
            values.add(v)
        }
        WorkbookFactory.create(true).use { w ->
            dateStyle = w.createCellStyle().apply {
                dataFormat = w.creationHelper.createDataFormat().getFormat("dd/MM/yyyy")
            }
            w.createSheet("Progetti", pncHeader, values)
            w.createSheet("PDA", pncPDAHeader, pda)
//            println("Autosizing columns")
//            (0 until pncHeader.values.count()).forEach(s::autoSizeColumn)
            calc.printGraphicOutput("Saving file")
            output.outputStream().use { o ->
                w.write(o)
                o.flush()
            }
        }
        calc.done()
    }

    private fun Workbook.createSheet(label: String, header: HeaderIndex, values: List<HeaderValues>) {
        val s0 = createSheet(label)
        var r = 0
        calc.restart()
        calc.maximum = values.size.toLong()
        s0.createRow(r++).apply {
            header.values.forEachIndexed { c, v ->
                createCell(c).setCellValue(v)
            }
        }
        values.forEach { value ->
            calc.doStep()
            calc.printGraphicOutput("Creating workbook sheet $label")
            s0.createRow(r++)
                .apply {
                    header.values.forEachIndexed { c, v ->
                        createCell(header, c, value[v])
                    }
                }
        }
    }

    //#region READER
    private fun readDefault(file: File, header: HeaderIndex): Map<CIP, HeaderValues> {
        file.inputStream().use { i ->
            val workbook = WorkbookFactory.create(i)
            val sheet = workbook.getSheetAt(0)
            val values = mutableMapOf<CIP, HeaderValues>()
            for ((index, row) in sheet.withIndex()) {
                if (index < 2)
                    continue
                try {
                    val v = header.newValues()
                    header.values.forEach { label ->
                        v[label] = row.getCell(header[label])?.toString()
                    }
                    values[v.cip!!] = v
                } catch (e: Exception) {
                    println("Error at row $index of file $file")
                    e.printStackTrace()
                    throw e
                }
            }
            return values
        }
    }

    private fun readMultiple(file: File, header: HeaderIndex): Map<CIP, List<HeaderValues>> {
        file.inputStream().use { i ->
            val workbook = WorkbookFactory.create(i)
            val sheet = workbook.getSheetAt(0)
            val values = mutableMapOf<CIP, MutableList<HeaderValues>>()
            var lastCip: CIP? = null
            for ((index, row) in sheet.withIndex()) {
                if (index < 2)
                    continue
                try {
                    val v = header.newValues()
                    header.values.forEach { label ->
                        v[label] = row.getCell(header[label])?.toString()
                    }
                    if (v.cip.isNullOrBlank())
                        v.cip = lastCip
                    else
                        lastCip = v.cip!!
                    values.computeIfAbsent(v.cip!!) { mutableListOf() }.add(v)
                } catch (e: Exception) {
                    println("Error at row $index of file $file")
                    e.printStackTrace()
                    throw e
                }
            }
            return values
        }
    }

    private fun readCosti(file: File): Map<CIP, HeaderValues> = readDefault(file, headerCupCosti)

    private fun readFasi(file: File): Map<CIP, List<HeaderValues>> = readMultiple(file, headerCupFasi)

    private fun readMisure(file: File): Map<CIP, HeaderValues> = readDefault(file, headerCupMisure)

    private fun readPDA(file: File): List<HeaderValues> = readMultiple(file, headerCupPDA).values.flatten()
    //#endregion

    private fun Row.createCell(header: HeaderIndex = pncHeader, index: Int, value: String?) {
        val label = header.values.find { header[it] == index }!!
        when {
            label.startsWith("Data") ->
                createCell(index).apply {
                    cellStyle = dateStyle
                    setCellValue(
                        value.takeUnless {
                            it.isNullOrEmpty() || it.startsWith("00") || it.startsWith("#")
                        }?.let { s ->
                            if (s.contains("/"))
                                dateFormatterIta.parse(s)
                            else
                                dateFormatterEng.parse(s)
                        }
                    )
                }

            value.isNumber() && index != 0 ->
                createCell(index).setCellValue(value!!.toDouble())

            else ->
                createCell(index).setCellValue(value)
        }
    }

    private fun fixSoggetto(soggetto: String?): String? {
        if (soggetto == "ASSISI")
            return "COMUNE DI ASSISI"
        return soggetto
    }

    private fun mergeCosto(pnc: HeaderValues, costo: HeaderValues, tipoFondo: String) {
        pnc.cip = costo.cip
        pnc["Descrizione progetto"] = costo["Descrizione progetto"]
        pnc.cup = costo.cup!!
        pnc["Natura CUP"] = costo["Natura CUP"]
        pnc["CLP"] = costo["CLP"]
        pnc["Soggetto attuatore"] = fixSoggetto(costo["Soggetto attuatore"])
        pnc["Ente beneficiario"] = pnc["Soggetto attuatore"]
        pnc["Data inizio prevista"] = costo["Data inizio prevista"]
        pnc["Data inizio effettiva"] = costo["Data inizio effettiva"]
        pnc["Data fine prevista"] = costo["Data fine prevista"]
        pnc["Data fine effettiva"] = costo["Data fine effettiva"]
        pnc["Stato avanzamento"] = costo["Stato avanzamento"]
        pnc["Esito prevalidazione"] = costo["Esito prevalidazione"]
        pnc["Data ultima prevalidazione"] = costo["Data ultima prevalidazione"]
        pnc["Esito validazione"] = costo["Esito validazione"]
        pnc["Data ultima validazione"] = costo["Data ultima validazione"]
        //pnc["Provincia"] = costo["Provincia"]
        pnc["PNRR - Risorse finanziarie"] = costo["Risorse finanziarie"]
        pnc["PNRR - Impegni totali"] = costo["Impegni totali"]
        pnc["PNRR - Pagamenti totali"] = costo["Pagamenti totali"]
        pnc["PNRR - Importo da realizzare"] = costo["Importo da realizzare"]
        pnc["PNRR - Importo realizzato nell'anno"] = costo["Importo realizzato nell’anno"]
        pnc["PNRR - Finanziamento totale"] = costo["Finanziamento totale"]
        pnc["PNRR - Finanziamento"] = costo["Finanziamento PNRR"]
        pnc["Importo pagamento validato RGS"] = costo["Importo pagamento validato RGS"]
        pnc["Finanziamento Stato FOI"] = costo["Finanziamento Stato FOI"]

        // FIXME pnc["Percentuale localizzazione"] = costo["PNRR - Percentuale localizzazione"]
        pnc["Tipo fondo"] = tipoFondo
    }

    private fun mergeFase(pnc: HeaderValues, fase: HeaderValues, numFase: Int) {
        pnc["Fase procedurale $numFase"] = fase["Codice fase"]
        pnc["Descrizione fase $numFase"] = fase["Fase procedurale"]
        pnc["Data inizio prevista fase $numFase"] = fase["Data inizio prevista fase"]
        pnc["Data inizio effettiva fase $numFase"] = fase["Data inizio effettiva fase"]
        pnc["Data fine prevista fase $numFase"] = fase["Data fine prevista fase"]
        pnc["Data fine effettiva fase $numFase"] = fase["Data fine effettiva fase"]

        pnc["Milestone $numFase"] = fase["Fase procedurale"]
        pnc["Data milestone $numFase"] = fase["Data fine prevista fase"]
    }

    private fun mergeMisura(pnc: HeaderValues, misura: HeaderValues) {
        pnc["Missione"] = misura["PNRR - Missione"]
        pnc["Componente"] = misura["PNRR - Componente"]
        pnc["Descrizione componente"] = misura["PNRR - Descrizione componente"]
        pnc["Submisura"] = misura["PNRR - Submisura"]
        pnc["Descrizione submisura"] = misura["PNRR - Descrizione submisura"]
        pnc["Misura"] = misura["PNRR - Misura"]
        pnc["Descrizione misura"] = misura["PNRR - Descrizione misura"]
        pnc["Percentuale localizzazione"] =
            misura["PNRR - Percentuale localizzazione"] // FIXME this should be taken from costi
    }

    private val HeaderValues.tipoFondo: String
        get() {
            return if (this["PNRR - Missione"] == "#")
                "PNC"
            else
                "PNRR"
        }

    private fun String?.isNumber(): Boolean {
        try {
            if (this != null) {
                this.toDouble()
                return true
            }
        } catch (_: Exception) {
        }
        return false
    }
}

