package it.umbria.regione.cdc.db

import it.umbria.regione.db.*

class CdCTable(name:String) : DBTable(name)

val table = CdCTable("CdCProgetti").apply {
    with(columns) {
        add(ID)
        add(DBColumn("CUP", DBVarChar(15), unique = true, notNull = true))
        add(IMPORT_DATE)
        add(DBColumn("enteSoggettoAttuatore", DBBoolean))
        add(DBColumn("enteSoggettoAttuatoreNote", DBVarchar))
        add(DBColumn("statoCUP", DBEnum("Attivo", "Chiuso", "Revocato", "Cancellato")))
        add(DBColumn("tipoFinanziamento", DBVarchar))
        add(DBColumn("statoProgetto", DBEnum("Avviato", "Non avviato", "Concluso")))
        add(DBColumn("statoProgettoNote", DBVarchar))
        add(DBColumn("progettoInEssere", DBBoolean))
        add(DBColumn("statoFinanziamento", DBVarchar))
        add(DBColumn("statoFinanziamentoNote", DBVarchar))
        add(DBColumn("collegamentoAltriCUP", DBVarchar, notNull = false))
        add(DBColumn("codiceIdentificativoProgetto", DBVarchar, notNull = false))

        add(DBColumn("missione", DBVarChar(3)))
        add(DBColumn("componente", DBVarChar(10)))
        add(DBColumn("misura", DBVarChar(30)))
        add(DBColumn("submisura", DBVarChar(30)))
        add(DBColumn("pnc", DBVarChar(10)))
        add(DBColumn("descrizione", DBVarchar))
        add(DBColumn("scadenzaNazionale", DBVarchar, notNull = false))
        add(DBColumn("costoProgetto", DBDouble))
        add(DBColumn("importoFinanziato", DBDouble))

        add(DBColumn("presenzaREGIS", DBBoolean))
        add(DBColumn("presenzaREGISNote", DBVarchar))
        add(DBColumn("enteStrumentale", DBVarchar, notNull = false))

        add(DBColumn("importoFinanziamentoPNRR", DBDouble))
        add(DBColumn("importoFinanziamentoPNC", DBDouble))
        add(DBColumn("importoFinanziamentoFOI", DBDouble))
        add(DBColumn("importoFinanziamentoAltraFontePubblica", DBDouble))
        add(DBColumn("importoQuotaRisorseProprie", DBDouble))
        add(DBColumn("fonteRisorseProprie", DBVarchar, notNull = false))
        add(DBColumn("risorsePrivate", DBDouble))
        add(DBColumn("costoInizialeRimodulato", DBBoolean))
        add(DBColumn("anticipazionePNRR", DBDouble))

        add(DBColumn("accertamentiTotali", DBDouble))
        add(DBColumn("accertamentiTrasferimentiPNRRPNC", DBDouble))
        add(DBColumn("entrataFPV", DBDouble))
        add(DBColumn("entrataFPVAnticipazionePNRR", DBDouble))
        add(DBColumn("utilizzoDisavanzoVincolato", DBDouble))
        add(DBColumn("utilizzoDisavanzoVincolatoPNRRPNR", DBDouble))
        add(DBColumn("impegniTotali", DBDouble))
        add(DBColumn("impegniTotaliPNRRPNC", DBDouble))
        add(DBColumn("spesaFPV", DBDouble))
        add(DBColumn("spesaFPVAnticipazionePNRR", DBDouble))
        add(DBColumn("avanzoVincolato2023", DBDouble))
        add(DBColumn("avanzoVincolatoPNRRPNC", DBDouble))
        add(DBColumn("pagamentiTotali", DBDouble))
        add(DBColumn("pagamentiTotaliPNRRPNC", DBDouble))

        add(DBColumn("ultimaFase", DBVarchar))
        add(DBColumn("ultimaFaseNote", DBVarchar))
        add(DBColumn("dataFinePrevista", DBDate))
        add(DBColumn("dataFineEffettiva", DBDate))

        add(DBColumn("criticitàRiscontrateRealizzazione", DBVarchar, notNull = false))
        add(DBColumn("criticitàRiscontrateRendicontazione", DBVarchar, notNull = false))
    }
}
