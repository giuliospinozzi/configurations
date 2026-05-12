package it.umbria.regione.openpnrr

import kotlinx.serialization.Serializable

@Serializable
data class Scadenza(
    val id: Int,
    val tipologia: String,
    val numero_sequenziale: String,
    val descrizione_breve: String,
    val descrizione_completa: String,
    val tempistica_completamento_anno: Int,
    val tempistica_completamento_trimestre: String,
    val ita_ue: String,
    val misure: List<Misura>,
    val status: String,
    val status_ultima_data: String?,
)

@Serializable
data class Misura(val id: Int,
                  val url: String,
                  val descrizione: String,
                  val codice_identificativo: String)

@Serializable
data class Organizzazione(val id: Int,
                  val denominazione: String)

@Serializable
data class Progetto(
    val url: String,
    val id: Int = url.substringAfterLast("/").toInt(),
    val pagamenti: List<Pagamento> = emptyList(),
    val titolo: String,
    val cup: String,
    val finanziamento_pnrr: Double,
    val finanziamento_pnc: Double,
    val finanziamento_stato_foi: Double,
    val finanziamento_totale: Double,
    val soggetto_attuatore: String,
    val data_inizio_progetto_prevista: String?,
    val data_inizio_progetto_effettiva: String?,
    val data_fine_progetto_prevista: String?,
    val data_fine_progetto_effettiva: String?,
    val codice_fase_iter_progetto: String?,
    val descrizione_fase_iter_progetto: String?,
    val stato_fase_iter_progetto: String?,
    val misura: String,
    val territori: List<String>,
)

@Serializable
data class Pagamento(val id: Int,
                     val pagamento_tot: Double,
                     val pagamento_pnrr: Double,
                     val pagamento_pnc: Double,
                     val pagamento_regione: Double,
                     val pagamento_privato: Double,
                     val pagamento_altri_fondi: Double,
                     val data_aggiornamento: String,
                     val progetto: Int)


@Serializable
data class Territorio(val id: Int,
                      val parent: String?,
                      val denominazione: String,
                      val tipologia: String)