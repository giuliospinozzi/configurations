package it.umbria.regione.cdc.io

import it.umbria.regione.cdc.db.CdCDatabase
import it.umbria.regione.cdc.db.table
import it.umbria.regione.config.DataImporterParameters
import it.umbria.regione.db.IMPORT_DATE
import it.umbria.regione.io.ExcelToDatabaseImporter
import it.umbria.regione.model.HeaderIndex
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ImportCdCFromExcel(db: CdCDatabase) : ExcelToDatabaseImporter(db) {

    private val emptyNote = "Campo note libero"

    private val String.ifValid: String?
        get() = if (this.isBlank() || this == emptyNote) null else this

    private fun File.listRecursive(): List<File> {
        val files = mutableListOf<File>()
        listFiles { f -> f.extension == "xlsx" }?.forEach {
            if (it.isDirectory) {
                files.addAll(it.listRecursive())
            } else {
                files.add(it)
            }
        }
        return files
    }

    private val indexDati = 3
    private val indexNote = 4
    private val indexPrecomp = 5

    fun importFiles(dir: File, p: DataImporterParameters) {
        prepareDatabase(p)
        dir.listRecursive().forEach { file ->
            println("Importing file ${file.absolutePath.substring(dir.absolutePath.length)}")
            file.inputStream().use { i ->
                val workbook = WorkbookFactory.create(i)
                for (s in workbook.sheetIterator().withIndex()) {
                    val sheet = s.value
                    table.createValues(s.index) { values ->
                        values["CUP"] = sheet.getRow(0).getCell(1).stringCellValue
                        println("\tImporting CUP ${values["CUP"]}")
                        values[IMPORT_DATE] = sheet.getRow(2).getCell(1).dateCellValue
                        var row = 7
                        values["enteSoggettoAttuatore"] =
                            sheet.getRow(row).getCell(indexDati).stringCellValue.startsWith("Sì")
                        values["enteSoggettoAttuatoreNote"] =
                            sheet.getRow(row++).getCell(indexNote).stringCellValue.ifValid
                        values["statoCUP"] = sheet.getRow(row++).getCell(indexDati).stringCellValue
                        values["tipoFinanziamento"] = sheet.getRow(row++).getCell(indexDati).stringCellValue
                        values["statoProgetto"] = sheet.getRow(row).getCell(indexDati).stringCellValue
                        values["statoProgettoNote"] = sheet.getRow(row++).getCell(indexNote).stringCellValue.ifValid
                        values["progettoInEssere"] =
                            sheet.getRow(row++).getCell(indexDati).stringCellValue.startsWith("Sì")
                        values["statoFinanziamento"] = sheet.getRow(row).getCell(indexDati).stringCellValue
                        values["statoFinanziamentoNote"] =
                            sheet.getRow(row++).getCell(indexNote).stringCellValue.ifValid
                        values["collegamentoAltriCUP"] = sheet.getRow(row++).getCell(indexNote).stringCellValue.ifValid
                        values["codiceIdentificativoProgetto"] =
                            sheet.getRow(row++).getCell(indexNote).stringCellValue.ifValid

                        row++
                        val comp = sheet.getRow(row++).getValidStringCell().stringCellValue
                        values["missione"] = if (comp.isNullOrEmpty()) null else comp.substring(0, 2)
                        values["componente"] = comp
                        values["misura"] = sheet.getRow(row++).getValidStringCell().stringCellValue
                        values["submisura"] = sheet.getRow(row++).getValidStringCell().stringCellValue
                        values["pnc"] = sheet.getRow(row++).getValidStringCell().stringCellValue
                        row++
                        values["descrizione"] = sheet.getRow(row++).getValidStringCell().stringCellValue
                        values["scadenzaNazionale"] = sheet.getRow(row++).getValidStringCell().stringCellValue.ifValid
                        values["costoProgetto"] = sheet.getRow(row++).getValidNumericCell().numericCellValue
                        values["importoFinanziato"] = sheet.getRow(row++).getValidNumericCell().numericCellValue

                        row++
                        values["presenzaREGIS"] = sheet.getRow(row).getCell(indexDati).stringCellValue.startsWith("Sì")
                        values["presenzaREGISNote"] = sheet.getRow(row++).getCell(indexNote).stringCellValue.ifValid
                        values["enteStrumentale"] = sheet.getRow(row++).getCell(indexNote).stringCellValue.ifValid

                        row++
                        values["importoFinanziamentoPNRR"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["importoFinanziamentoPNC"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["importoFinanziamentoFOI"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["importoFinanziamentoAltraFontePubblica"] =
                            sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["importoQuotaRisorseProprie"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["fonteRisorseProprie"] = sheet.getRow(row++).getCell(indexDati).stringCellValue.ifValid
                        values["risorsePrivate"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        row++
                        values["costoInizialeRimodulato"] =
                            sheet.getRow(row++).getCell(indexDati).stringCellValue.startsWith("Sì")

                        row++
                        values["anticipazionePNRR"] = sheet.getRow(row++).getCell(indexDati).numericCellValue

                        row++
                        values["accertamentiTotali"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["accertamentiTrasferimentiPNRRPNC"] =
                            sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["entrataFPV"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["entrataFPVAnticipazionePNRR"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["utilizzoDisavanzoVincolato"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["utilizzoDisavanzoVincolatoPNRRPNR"] =
                            sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["impegniTotali"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["impegniTotaliPNRRPNC"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["spesaFPV"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["spesaFPVAnticipazionePNRR"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["avanzoVincolato2023"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["avanzoVincolatoPNRRPNC"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["pagamentiTotali"] = sheet.getRow(row++).getCell(indexDati).numericCellValue
                        values["pagamentiTotaliPNRRPNC"] = sheet.getRow(row++).getCell(indexDati).numericCellValue

                        row++
                        values["ultimaFase"] = sheet.getRow(row).getCell(indexDati).stringCellValue
                        values["ultimaFaseNote"] = sheet.getRow(row++).getCell(indexPrecomp).stringCellValue.ifValid
                        values["dataFinePrevista"] = sheet.getRow(row++).getCell(indexNote).convertToDate()
                        values["dataFineEffettiva"] = sheet.getRow(row++).getCell(indexNote).convertToDate()

                        row++
                        values["criticitàRiscontrateRealizzazione"] =
                            sheet.getRow(row++).getCell(indexNote).stringCellValue.ifValid
                        values["criticitàRiscontrateRendicontazione"] =
                            sheet.getRow(row++).getCell(indexNote).stringCellValue.ifValid

                        db.insertData(table, values)
                        0
                    }
                }
                db.prepareInsert(table).executeBatch()
            }
        }
    }

    private fun Row.getValidStringCell() : Cell {
        if (getCell(indexPrecomp).stringCellValue.isNotEmpty())
            return getCell(indexPrecomp)
        return getCell(indexDati)
    }

    private fun Row.getValidNumericCell() : Cell {
        if (getCell(indexPrecomp).cellType == CellType.NUMERIC)
            return getCell(indexPrecomp)
        return getCell(indexDati)
    }

    private fun Cell.convertToDate(): Date? {
        if (cellType == CellType.NUMERIC) {
            return dateCellValue
        } else if (cellType == CellType.STRING) {
            val s = stringCellValue.replace("Calendario:", "").trim()
            return dateFormatter.parse(s)
        }
        return null
    }

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy")

    override val fileHeader: HeaderIndex = HeaderIndex()

}
