package it.umbria.regione.pnrr.db

import it.umbria.regione.db.*

class PNRRTable(name: String) : DBTable(name)

val tableSoggetti = PNRRTable("Soggetti").apply {
    with(columns) {
        add(ID)
        add(DBColumn("nome", notNull = true))
        add(DBColumn("provincia"))
    }
}

//#region Misure
//region PNRR
val tablePNRRMissioni = PNRRTable("PNRRMissioni").apply {
    with(columns) {
        add(DBColumn("nome", DBVarChar(3), primaryKey = true))
        add(DBColumn("descrizione", DBVarChar(1024)))
    }
}
val tablePNRRComponenti = PNRRTable("PNRRComponenti").apply {
    with(columns) {
        add(DBColumn("nome", DBVarChar(10), primaryKey = true))
        add(DBColumn("descrizione", DBVarChar(1024)))
        add(DBFK("parent", tablePNRRMissioni, "nome", required = true))
    }
}
val tablePNRRMisure = PNRRTable("PNRRMisure").apply {
    with(columns) {
        add(DBColumn("nome", DBVarChar(30), primaryKey = true))
        add(DBColumn("descrizione", DBVarChar(1024)))
        add(DBFK("parent", tablePNRRComponenti, "nome", required = true))
    }
}
val tablePNRRSubmisure = PNRRTable("PNRRSubmisure").apply {
    with(columns) {
        add(DBColumn("nome", DBVarChar(30), primaryKey = true))
        add(DBColumn("descrizione", DBVarChar(1024)))
        add(DBFK("parent", tablePNRRMisure, "nome", required = true))
    }
}
//endregion

//region PNC
val tablePNCMacroMisure = PNRRTable("PNCMacroMisure").apply {
    with(columns) {
        add(DBColumn("nome", DBVarChar(2), primaryKey = true))
        add(DBColumn("descrizione", DBVarChar(1024)))
    }
}
val tablePNCSubmisure = PNRRTable("PNCSubmisure").apply {
    with(columns) {
        add(DBColumn("nome", DBVarChar(4), primaryKey = true))
        add(DBColumn("descrizione", DBVarChar(1024)))
        add(DBFK("parent", tablePNCMacroMisure, "nome", required = true))
    }
}
val tablePNCLineeIntervento = PNRRTable("PNCLineeIntervento").apply {
    with(columns) {
        add(DBColumn("nome", DBVarChar(10), primaryKey = true))
        add(DBColumn("descrizione", DBVarChar(1024)))
        add(DBFK("parent", tablePNCSubmisure, "nome", required = true))
    }
}
//endregion

//region PNCSISMA
val tablePNCSISMAMacroMisure = PNRRTable("PNCSISMAMacroMisure").apply {
    with(columns) {
        add(DBColumn("nome", DBVarChar(2), primaryKey = true))
        add(DBColumn("descrizione", DBVarChar(1024)))
    }
}
val tablePNCSISMASubmisure = PNRRTable("PNCSISMASubmisure").apply {
    with(columns) {
        add(DBColumn("nome", DBVarChar(4), primaryKey = true))
        add(DBColumn("descrizione", DBVarChar(1024)))
        add(DBFK("parent", tablePNCSISMAMacroMisure, "nome", required = true))
    }
}
val tablePNCSISMALineeIntervento = PNRRTable("PNCSISMALineeIntervento").apply {
    with(columns) {
        add(DBColumn("nome", DBVarChar(10), primaryKey = true))
        add(DBColumn("descrizione", DBVarchar))
        add(DBFK("parent", tablePNCSISMASubmisure, "nome", required = true))
    }
}
//endregion
//#endregion

val tableFinanziamenti = PNRRTable("Finanziamenti").apply {
    with(columns) {
        add(ID)
        add(DBColumn("tipoFinanziamento", DBVarchar, notNull = true))
        add(DBColumn("risorseFinanziarie", DBDouble))
        add(DBColumn("impegniTotali", DBDouble))
        add(DBColumn("pagamentiTotali", DBDouble))
        add(DBColumn("importoDaRealizzare", DBDouble))
        add(DBColumn("importoRealizzatoAnno", DBDouble))
        add(DBColumn("finanziamentoTotale", DBDouble))
        add(DBColumn("finanziamento", DBDouble))
        add(DBColumn("importoPagamentoValidatoRGS", DBDouble))
        add(DBColumn("finanziamentoStatoFOI", DBDouble))
        add(DBColumn("percentualeLocalizzazione", DBDouble))
    }
}

val tableProgetti = PNRRTable("Progetti").apply {
    with(columns) {
        add(ID)
        add(IMPORT_DATE)
        add(DBColumn("codiceIdentificativoProgetto", DBVarchar))
        add(DBColumn("descrizioneProgetto", DBText))
        add(DBColumn("CUP", DBVarchar))
        add(DBColumn("naturaCUP", DBVarchar))
        add(DBColumn("CLP", DBVarchar))
        add(DBColumn("statoAvanzamento"))
        add(DBFK("soggettoAttuatore", tableSoggetti, required = true))
        add(DBColumn("dataInizioPrevista", DBDate))
        add(DBColumn("dataFinePrevista", DBDate))
        add(DBColumn("dataInizioEffettiva", DBDate))
        add(DBColumn("dataFineEffettiva", DBDate))
        //add(DBColumn("provincia", DBVarchar))

        add(DBColumn("tipoInvestimento"))
        add(DBColumn("tipoFondo"))

        add(DBFK("finanziamentoPNRR", tableFinanziamenti, unique = true))
        add(DBFK("finanziamentoPNC", tableFinanziamenti, unique = true))
        add(DBFK("finanziamentoPNCSISMA2016", tableFinanziamenti, unique = true))

        add(DBColumn("settore"))
        add(DBColumn("ministeroTitolare"))
        add(DBColumn("strutturaCompetenteAttuatore"))
        add(DBFK("enteBeneficiario", tableSoggetti))
        add(DBFK("soggettoRealizzatore", tableSoggetti, required = false))
        add(DBFK("altriSoggettiRealizzatori", tableSoggetti, required = false))
        add(DBColumn("programmaDelPiano", DBBoolean))
        add(DBColumn("programmaCofinanziato", DBBoolean))
        add(DBColumn("esitoPrevalidazione", DBBoolean))
        add(DBColumn("dataUltimaPrevalidazione", DBDate))
        add(DBColumn("esitoValidazione", DBBoolean))
        add(DBColumn("dataUltimaValidazione", DBDate))
    }
}

val tableProgettiInterventi = PNRRTable("ProgettiInterventi").apply {
    with(columns) {
        add(ID)
        add(DBFK("progetto", tableProgetti, required = true))
        add(DBColumn("riferimentoIntervento"))
        add(DBColumn("tipoRiferimentoIntervento"))
    }
}

//#region PDA
val tablePDA = PNRRTable("PDA").apply {
    with(columns) {
        add(ID)
        add(DBFK("progetto", tableProgetti, required = true))
        add(DBColumn("codiceInternoPDA", DBVarchar))
        add(DBColumn("dataPubblicazionePDA", DBDate))
        add(DBColumn("dataAggiudicazioneDefinitivaPDA", DBDate))
        add(DBColumn("tipoPDA"))
        add(DBColumn("CIG", DBVarchar))
        add(DBColumn("motivoAssenzaCIG", DBVarchar))
    }
}
//#endregion

//#region Fasi e milestone
val tableFasi = PNRRTable("Fasi").apply {
    with(columns) {
        add(ID)
        add(DBFK("progetto", tableProgetti, required = true))
        add(DBColumn("numeroFase", DBInt, notNull = true))
        add(DBColumn("codiceFase", DBVarchar))
        add(DBColumn("descrizione", DBVarchar))
        add(DBColumn("dataInizioPrevista", DBDate))
        add(DBColumn("dataFinePrevista", DBDate))
        add(DBColumn("dataInizioEffettiva", DBDate))
        add(DBColumn("dataFineEffettiva", DBDate))
    }
}

val tableMilestone = PNRRTable("Milestone").apply {
    with(columns) {
        add(ID)
        add(DBFK("progetto", tableProgetti, required = true))
        add(DBColumn("numeroMilestone", DBInt, notNull = true))
        add(DBColumn("descrizione", DBVarchar))
        add(DBColumn("data", DBDate))
        add(DBColumn("europea", DBBoolean))
    }
}
//#endregion

val tableItaliaDomaniMilestones = PNRRTable("ItaliaDomaniMilestone").apply {
    with(columns) {
        add(ID)
        add(DBColumn("submisura", DBVarChar(30), notNull = true))
        add(DBColumn("descrizione", DBVarChar(1024)))
        add(DBColumn("titolare", DBVarchar))
        add(DBColumn("tipo", DBEnum("Milestone", "Target"), notNull = true))
        add(DBColumn("nomeTipo", DBText))
        add(DBColumn("descrizioneSpecifica", DBText))
        add(DBColumn("regione", DBEnum("ITA", "UE"), notNull = true))
        add(DBColumn("indicatoreQualitativo", DBText))
        add(DBColumn("unitàDiMisura", DBVarchar))
        add(DBColumn("riferimento", DBDouble))
        add(DBColumn("obiettivo", DBDouble))
        add(DBColumn("dataConseguimento", DBDate))
    }
}

val tablesList = arrayOf(
    tableSoggetti,
    tablePNRRMissioni,
    tablePNRRComponenti,
    tablePNRRMisure,
    tablePNRRSubmisure,
    tablePNCMacroMisure,
    tablePNCSubmisure,
    tablePNCLineeIntervento,
    tablePNCSISMAMacroMisure,
    tablePNCSISMASubmisure,
    tablePNCSISMALineeIntervento,
    tableFinanziamenti,
    tableProgetti,
    tableFasi,
    tableMilestone,
    tablePDA,
    tableProgettiInterventi,
)

data class InterventoKey(val key: String, val reference: DBTable)
