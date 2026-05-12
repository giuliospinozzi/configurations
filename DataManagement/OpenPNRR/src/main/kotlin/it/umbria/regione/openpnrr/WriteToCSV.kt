package it.umbria.regione.openpnrr

import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import kotlin.text.substringAfterLast

private fun String.createCsv(writer: (CSVWriter) -> Unit) {
    FileWriter(File(folder, this)).use { fileWriter ->
        val csvWriter = CSVWriter(fileWriter)
        writer(csvWriter)
        csvWriter.close()
    }
}

private fun String.appendCsv(vararg header: String, writer: (CSVWriter) -> Unit) {
    val file = File(folder, this)
    val withHeader = !file.exists() || file.length() == 0L
    FileWriter(file, true).use { fileWriter ->
        val csvWriter = CSVWriter(fileWriter)
        if (withHeader)
            csvWriter.writeNext(header)
        writer(csvWriter)
        csvWriter.close()
    }
}

fun writeOrganizzazioniToCsv(organizzazioni: List<Organizzazione>) {
    "organizzazioni.csv".appendCsv("id", "denominazione") { csvWriter ->
        for (organizzazione in organizzazioni) {
            csvWriter.writeNext(arrayOf(
                organizzazione.id.toString(),
                organizzazione.denominazione
            ))
        }
    }
}

fun writeMisureToCsv(misure: List<Misura>) {
    "misure.csv".createCsv { csvWriter ->
        csvWriter.writeNext(arrayOf("id", "descrizione", "codiceIdentificativo"))
        for (misura in misure) {
            csvWriter.writeNext(arrayOf(
                misura.id.toString(),
                misura.descrizione,
                misura.codice_identificativo
            ))
        }
    }
}

fun writeProgettiToCsv(progetti: List<Progetto>) {
    "progetti.csv".appendCsv("id", "titolo", "cup", "finanziamento_pnrr","finanziamento_pnc", "finanziamento_stato_foi",
        "finanziamento_totale", "id_soggetto_attuatore", "data_inizio_progetto_prevista","data_inizio_progetto_effettiva","data_fine_progetto_prevista","data_fine_progetto_effettiva","codice_fase_iter_progetto","descrizione_fase_iter_progetto","stato_fase_iter_progetto", "id_misura") { csvWriter ->
        for (progetto in progetti) {
            csvWriter.writeNext(arrayOf(
                progetto.id.toString(),
                progetto.titolo,
                progetto.cup,
                progetto.finanziamento_pnrr.toString(),
                progetto.finanziamento_pnc.toString(),
                progetto.finanziamento_stato_foi.toString(),
                progetto.finanziamento_totale.toString(),
                progetto.soggetto_attuatore.substringAfterLast("/"),
                progetto.data_inizio_progetto_prevista,
                progetto.data_inizio_progetto_effettiva,
                progetto.data_fine_progetto_prevista,
                progetto.data_fine_progetto_effettiva,
                progetto.codice_fase_iter_progetto,
                progetto.descrizione_fase_iter_progetto,
                progetto.stato_fase_iter_progetto,
                progetto.misura.substringAfterLast("/")
            ))
        }
    }
    "progetti_territori.csv".appendCsv("progetto", "territorio") { csvWriter ->
        for (progetto in progetti) {
            progetto.territori.forEach { territorio ->
                csvWriter.writeNext(arrayOf(
                    progetto.id.toString(),
                    territorio.substringAfterLast("/")
                ))
            }
        }
    }
    "progetti_pagamenti.csv".appendCsv("progetto", "pagamento_tot", "pagamento_pnrr",
        "pagamento_pnc", "pagamento_regione", "pagamento_privato", "pagamento_altri_fondi", "data_aggiornamento") { csvWriter ->
        for (progetto in progetti) {
            progetto.pagamenti.forEach { pagamento ->
                csvWriter.writeNext(arrayOf(
                    progetto.id.toString(),
                    pagamento.pagamento_tot.toString(),
                    pagamento.pagamento_pnrr.toString(),
                    pagamento.pagamento_pnc.toString(),
                    pagamento.pagamento_regione.toString(),
                    pagamento.pagamento_privato.toString(),
                    pagamento.pagamento_altri_fondi.toString(),
                    pagamento.data_aggiornamento.toString()
                ))
            }
        }
    }
}

fun writeTerritoriToCsv(territori: List<Territorio>) {
    "territori.csv".createCsv { csvWriter ->
        csvWriter.writeNext(arrayOf("id", "parent_id", "denominazione", "tipologia"))
        for (territorio in territori) {
            if (territorio.parent.isNullOrEmpty()){
                csvWriter.writeNext(arrayOf(
                    territorio.id.toString(),
                    territorio.parent,
                    territorio.denominazione,
                    territorio.tipologia))
            } else{
            csvWriter.writeNext(arrayOf(
                territorio.id.toString(),
                territorio.parent.substringAfterLast("/"),
                territorio.denominazione,
                territorio.tipologia
            ))}
        }
    }
}

fun writeScadenzeToCsv(scadenze: List<Scadenza>) {
    val misure = LinkedHashMap<Int, List<Misura>>()
    "scadenze.csv".createCsv { csvWriter ->
        csvWriter.writeNext(arrayOf("id", "tipologia", "numeroSequenziale",
            "descrizioneBreve", "descrizioneCompleta", "annoCompletamento", "trimestreCompletamento",
            "ITA_UE", "status", "dataUltimaModifica"))

        for (scadenza in scadenze) {
            csvWriter.writeNext(arrayOf(
                scadenza.id.toString(),
                scadenza.tipologia,
                scadenza.numero_sequenziale,
                scadenza.descrizione_breve,
                scadenza.descrizione_completa,
                scadenza.tempistica_completamento_anno.toString(),
                scadenza.tempistica_completamento_trimestre,
                scadenza.ita_ue,
                scadenza.status,
                scadenza.status_ultima_data
            ))
            misure[scadenza.id] = scadenza.misure
        }
    }
    "scadenze_misure.csv".createCsv { csvWriter ->
        csvWriter.writeNext(arrayOf("scadenza", "misura"))
        for ((id, list) in misure) {
            list.forEach { misura ->
                csvWriter.writeNext(arrayOf(
                    id.toString(),
                    misura.codice_identificativo
                ))
            }
        }
    }
}
