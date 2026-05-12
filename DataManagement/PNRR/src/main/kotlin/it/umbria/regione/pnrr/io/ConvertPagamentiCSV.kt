package it.umbria.regione.pnrr.io

import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import it.umbria.regione.pnrr.defaultInputDir
import java.io.File

fun main() {
    val dir = File(defaultInputDir, "../DatiFinanziari")
    val input = File(dir, "Pagamenti.csv")
    val output = File(dir, "Pagamenti_out.csv")
    val headerOut = listOf("CUP",
        "Anno",
        "Tipo",
        "Capitolo",
        "Importo",
        "Data")
    CSVReader(input.reader()).use { cin ->
        cin.readNext()
        CSVWriter(output.writer()).use { cout ->
            cout.writeNext(headerOut.toTypedArray())
            cin.forEach { line ->
                var i = 0
                val cup = line[i++]
                val capitolo = line[i++]
                val anticipo2021 = line[i++]
                val dataAnticipo2021 = line[i++]
                val anticipo2022 = line[i++]
                val dataAnticipo2022 = line[i++]
                val anticipo2023 = line[i++]
                val dataAnticipo2023 = line[i++]
                val anticipo2024 = line[i++]
                val dataAnticipo2024 = line[i++]
                val pagamenti2022 = line[i++]
                val dataPagamenti2022 = line[i++]
                val pagamenti2023 = line[i++]
                val dataPagamenti2023 = line[i++]
                val pagamenti2024 = line[i++]
                val dataPagamenti2024 = line[i++]
                if (anticipo2021.isNotBlank()) {
                    cout.writeNext(arrayOf(cup, "2021", "Anticipo", capitolo, anticipo2021, dataAnticipo2021))
                }
                if (anticipo2022.isNotBlank()) {
                    cout.writeNext(arrayOf(cup, "2022", "Anticipo", capitolo, anticipo2022, dataAnticipo2022))
                }
                if (anticipo2023.isNotBlank()) {
                    cout.writeNext(arrayOf(cup, "2023", "Anticipo", capitolo, anticipo2023, dataAnticipo2023))
                }
                if (anticipo2024.isNotBlank()) {
                    cout.writeNext(arrayOf(cup, "2024", "Anticipo", capitolo, anticipo2024, dataAnticipo2024))
                }
                if (pagamenti2022.isNotBlank()) {
                    cout.writeNext(arrayOf(cup, "2022", "Pagamento", capitolo, pagamenti2022, dataPagamenti2022))
                }
                if (pagamenti2023.isNotBlank()) {
                    cout.writeNext(arrayOf(cup, "2023", "Pagamento", capitolo, pagamenti2023, dataPagamenti2023))
                }
                if (pagamenti2024.isNotBlank()) {
                    cout.writeNext(arrayOf(cup, "2024", "Pagamento", capitolo, pagamenti2024, dataPagamenti2024))
                }
            }
            cout.flush()
        }
    }
}
