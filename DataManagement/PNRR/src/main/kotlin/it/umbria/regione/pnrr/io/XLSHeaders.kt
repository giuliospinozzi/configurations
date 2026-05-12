package it.umbria.regione.pnrr.io

import it.umbria.regione.model.HeaderIndex
import it.umbria.regione.model.HeaderValues

const val pnrrNumberOfFasi = 12

internal const val cupLabel = "CUP"
internal const val cipLabel = "Codice Identificativo Progetto"

internal fun createPncHeader(numberOfFasi: Int = pnrrNumberOfFasi): HeaderIndex {
    var pncI = 0

    //#region Inner functions
    fun risorse(type: String): Array<Pair<String, Int>> {
        return arrayOf(
            "$type - Risorse finanziarie" to pncI++,
            "$type - Impegni totali" to pncI++,
            "$type - Pagamenti totali" to pncI++,
            "$type - Importo da realizzare" to pncI++,
            "$type - Importo realizzato nell'anno" to pncI++,
            "$type - Finanziamento totale" to pncI++,
            "$type - Finanziamento" to pncI++
        )
    }

    fun fasi(numeroFasi: Int): Array<Pair<String, Int>> {
        return (1..numeroFasi).flatMap { i ->
            listOf(
                "Fase procedurale $i" to pncI++,
                "Descrizione fase $i" to pncI++,
                "Data inizio prevista fase $i" to pncI++,
                "Data inizio effettiva fase $i" to pncI++,
                "Data fine prevista fase $i" to pncI++,
                "Data fine effettiva fase $i" to pncI++
            )
        }.toTypedArray()
    }

    fun milestone(numeroMilestone: Int): Array<Pair<String, Int>> {
        return (1..numeroMilestone).flatMap { i ->
            listOf(
                "Milestone $i" to pncI++,
                "Data milestone $i" to pncI++,
                "Milestone $i europea" to pncI++
            )
        }.toTypedArray()
    }
    //#endregion
    return HeaderIndex(
        cipLabel to pncI++,
        "Descrizione progetto" to pncI++,
        cupLabel to pncI++,
        "Natura CUP" to pncI++,
        "CLP" to pncI++,
        "Stato avanzamento" to pncI++,
        "Soggetto attuatore" to pncI++,

        "Data inizio prevista" to pncI++,
        "Data fine prevista" to pncI++,
        "Data inizio effettiva" to pncI++,
        "Data fine effettiva" to pncI++,
        // "Provincia" to pncI++,

        // region Missione PNRR
        "Missione" to pncI++,
        "Componente" to pncI++,
        "Descrizione componente" to pncI++,
        "Misura" to pncI++,
        "Descrizione misura" to pncI++,
        "Submisura" to pncI++,
        "Descrizione submisura" to pncI++,
        // endregion

        "Tipo investimento" to pncI++,
        "Tipo fondo" to pncI++,

        // region Risorse Finanziarie PNRR
        *risorse("PNRR"),
        "Percentuale localizzazione" to pncI++,
        "Importo pagamento validato RGS" to pncI++,
        "Finanziamento Stato FOI" to pncI++,
        // endregion

        // region Fasi
        *fasi(numberOfFasi),
        // endregion
        // region Milestone
        *milestone(numberOfFasi),
        // endregion

        // region Risorse Finanziarie PNC
        *risorse("PNC"),
        // endregion

        // region PNC Intervento
        "PNC Macro misura" to pncI++,
        "PNC Descrizione macro misura" to pncI++,
        "PNC Submisura" to pncI++,
        "PNC Descrizione submisura" to pncI++,
        "PNC Linea intervento" to pncI++,
        "PNC Descrizione linea intervento" to pncI++,
        // endregion

        // region Risorse Finanziarie PNC SISMA 2016
        *risorse("PNC SISMA 2016"),
        // endregion

        // region PNC SISMA Intervento
        "PNC SISMA 2016 Macro misura" to pncI++,
        "PNC SISMA 2016 Descrizione macro misura" to pncI++,
        "PNC SISMA 2016 Submisura" to pncI++,
        "PNC SISMA 2016 Descrizione submisura" to pncI++,
        "PNC SISMA 2016 Linea intervento" to pncI++,
        "PNC SISMA 2016 Descrizione linea intervento" to pncI++,
        // endregion

        "Settore" to pncI++,
        "Ministero titolare" to pncI++,
        "Struttura competente attuatore" to pncI++,
        "Ente beneficiario" to pncI++,
        "Soggetto realizzatore" to pncI++,
        "Altri soggetti" to pncI++,
        "Programma del piano" to pncI++,
        "Programma cofinanziato" to pncI++,
        "Esito prevalidazione" to pncI++,
        "Data ultima prevalidazione" to pncI++,
        "Esito validazione" to pncI++,
        "Data ultima validazione" to pncI++
    )
}


var pncPDAI = 0
internal val pncPDAHeader = HeaderIndex(
    cipLabel to pncPDAI++,
    cupLabel to pncPDAI++,
    "Codice interno PDA" to pncPDAI++,
    "Data pubblicazione PDA" to pncPDAI++,
    "Data aggiudicazione definitiva PDA" to pncPDAI++,
    "Tipo PDA" to pncPDAI++,
    "CIG" to pncPDAI++,
    "Motivo assenza CIG" to pncPDAI++
)


var costiI = 0
internal val headerCupCosti = HeaderIndex(
    cipLabel to costiI++,
    "Descrizione progetto" to costiI++,
    cupLabel to costiI++,
    "Natura CUP" to costiI++,
    "CLP" to costiI++,
    "Soggetto attuatore" to costiI++,
    "Data inizio prevista" to costiI++,
    "Data inizio effettiva" to costiI++,
    "Data fine prevista" to costiI++,
    "Data fine effettiva" to costiI++,
    "Stato avanzamento" to costiI++,
    "Esito prevalidazione" to costiI++,
    "Data ultima prevalidazione" to costiI++,
    "Esito validazione" to costiI++,
    "Data ultima validazione" to costiI++,
    //"Provincia" to costiI++,
    "Risorse finanziarie" to costiI++,
    "Impegni totali" to costiI++,
    "Pagamenti totali" to costiI++,
    "Importo pagamento validato RGS" to costiI++,
    "Importo da realizzare" to costiI++,
    "Importo realizzato nell’anno" to costiI++,
    "Finanziamento totale" to costiI++,
    "Finanziamento PNRR" to costiI++,
    "Finanziamento Stato FOI" to costiI++
    //FIXME , "PNRR - Percentuale localizzazione" to costiI++
)

var fasiI = 0
internal val headerCupFasi = HeaderIndex(
    cipLabel to fasiI++,
    "Descrizione progetto" to fasiI++,
    cupLabel to fasiI++,
    "CLP" to fasiI++,
    "Codice fase" to fasiI++,
    "Fase procedurale" to fasiI++,
    "Data inizio effettiva fase" to fasiI++,
    "Data inizio prevista fase" to fasiI++,
    "Data fine prevista fase" to fasiI++,
    "Data fine effettiva fase" to fasiI++
)
internal val headerCupMisure = HeaderIndex(
    cipLabel to 0,
    cupLabel to 2,
    "CLP" to 3,
    "PNRR - Submisura" to 4,
    "PNRR - Descrizione submisura" to 5,
    "PNRR - Missione" to 6,
    "PNRR - Componente" to 7,
    "PNRR - Descrizione componente" to 8,
    "PNRR - Misura" to 9,
    "PNRR - Descrizione misura" to 10,
    "PNRR - Percentuale localizzazione" to 11 // FIXME this should be taken from costi
)

internal val headerCupPDA = HeaderIndex(
    cipLabel to 0,
    cupLabel to 2,
    "Codice interno PDA" to 3,
    "Data pubblicazione PDA" to 4,
    "Data aggiudicazione definitiva PDA" to 5,
    "Tipo PDA" to 6,
    "CIG" to 7,
    "Motivo assenza CIG" to 8
)

internal var HeaderValues.cup: String?
    get() = this[cupLabel]
    set(value) {
        this[cupLabel] = value
    }

internal var HeaderValues.cip: String?
    get() = this[cipLabel]
    set(value) {
        this[cipLabel] = value
    }


internal val headerItaliaDomaniMilestones = HeaderIndex(
    "submisura" to 8,
    "descrizione" to 10,
    "titolare" to 11,
    "tipo" to 13,
    "nomeTipo" to 14,
    "descrizioneSpecifica" to 15,
    "regione" to 16,
    "indicatoreQualitativo" to 17,
    "unitàDiMisura" to 18,
    "riferimento" to 19,
    "obiettivo" to 20,
    "dataConseguimento" to 24
)
