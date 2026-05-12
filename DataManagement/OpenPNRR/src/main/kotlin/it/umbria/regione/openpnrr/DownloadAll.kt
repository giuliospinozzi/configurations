package it.umbria.regione.openpnrr


// Funzione principale
fun main() {
    println("🚀 Starting download from page $fromPage to page ${fromPage + pageCount - 1}")

    if (fromPage==1) {
        downloadOpenPNRR<Misura>("misure") { writeMisureToCsv(it) }
        downloadOpenPNRR<Territorio>("territori") { writeTerritoriToCsv(it) }
        downloadRecursiveOpenPNRR<Scadenza>("scadenze") { writeScadenzeToCsv(it) }
    }

    if (fromPage<100) {
        downloadRecursiveOpenPNRR<Organizzazione>("organizzazioni") { writeOrganizzazioniToCsv(it) }
    }
   downloadRecursiveOpenPNRR<Progetto>("progetti") { writeProgettiToCsv(it) }

    println("✅ Data written")
}
