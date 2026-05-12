package it.umbria.regione.pnrr.db

import it.umbria.regione.config.DatabaseParameters
import it.umbria.regione.db.DBTable
import it.umbria.regione.db.XLSDatabase
import it.umbria.regione.model.ModelDate
import it.umbria.regione.model.toModelDate
import it.umbria.regione.pnrr.CUP

class PNRRDatabase(database: DatabaseParameters) : XLSDatabase(database) {

    override val tables: Array<PNRRTable>
        get() = tablesList

    fun createDefaultMissions() {
        val missions = arrayOf(
            "M1" to "Digitalizzazione, innovazione, competitività e cultura",
            "M2" to "Rivoluzione verde e transizione ecologica",
            "M3" to "Infrastrutture per la mobilità sostenibile",
            "M4" to "Istruzione e ricerca",
            "M5" to "Inclusione e coesione",
            "M6" to "Salute"
        )
        if (!conn.createStatement().use {
                it.executeQuery("SELECT COUNT(*) FROM ${tablePNRRMissioni.name};").use { r ->
                    r.next() && r.getInt(1) > 0
                }
            })
            missions.map {
                insertData(tablePNRRMissioni, "nome" to it.first, "descrizione" to it.second)
            }.distinct().single().executeBatch()
    }

    fun lastDateOf(cup: CUP): ModelDate? {
        val sql = """
            SELECT MAX(dataAggiornamento) AS lastDate
            FROM ${tableProgetti.name}
            WHERE CUP = '$cup';
        """.trimIndent()
        return conn.createStatement().use {
            it.executeQuery(sql).use { r ->
                if (r.next()) r.getDate("lastDate")?.toModelDate() else null
            }
        }
    }

    fun collectIntervento(tables: Array<DBTable>, values: Array<Pair<String?, String?>>): InterventoKey {
        assert(tables.size == values.size) { "Tables and values must have the same size" }
        var key: InterventoKey? = null
        for (i in tables.indices) {
            val t = tables[i]
            val (n, d) = values[i]
            if (n != null) {
                existsOrCreate(t, "nome", n) {
                    val v = mutableListOf("nome" to n, "descrizione" to d)
                    if (key != null)
                        v.add("parent" to key!!.key)
                    insertDataWithKey(this, *v.toTypedArray())
                }
                key = InterventoKey(n, t)
            }
        }
        return key!!
    }

    fun collectPNRRIntervento(
        missione: String,
        componente: String?,
        descrizioneComponente: String?,
        misura: String?,
        descrizioneMisura: String?,
        submisura: String?,
        descrizioneSubmisura: String?
    ): InterventoKey {
        existsOrCreate(tablePNRRMissioni, "nome", missione) {
            insertDataWithKey(this, "nome" to missione)
        }
        if (componente != null) {
            existsOrCreate(tablePNRRComponenti, "nome", componente) {
                insertDataWithKey(
                    this, "nome" to componente,
                    "descrizione" to descrizioneComponente,
                    "missione" to missione
                )
            }
            if (misura != null) {
                existsOrCreate(tablePNRRMisure, "nome", misura) {
                    insertDataWithKey(
                        this, "nome" to misura,
                        "descrizione" to descrizioneMisura,
                        "componente" to componente
                    )
                }
                if (submisura != null) {
                    existsOrCreate(tablePNRRSubmisure, "nome", submisura) {
                        insertDataWithKey(
                            this, "nome" to submisura,
                            "descrizione" to descrizioneSubmisura,
                            "misura" to misura
                        )
                    }
                    return InterventoKey(submisura, tablePNRRSubmisure)
                }
                return InterventoKey(misura, tablePNRRMisure)
            }
            return InterventoKey(componente, tablePNRRComponenti)
        }
        return InterventoKey(missione, tablePNRRMissioni)
    }
}
