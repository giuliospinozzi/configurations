package it.umbria.regione.openpnrr

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalTime
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

suspend fun downloadJson(url: String, progress: String = "", print: Boolean = debug): String =
    withContext(Dispatchers.IO) {
        if (print) {
            // format time as HH:mm:ss
            val now = LocalTime.now().toString().substring(0, 8)
            println("[$now] $progress 🔽 Downloading data from: $url")
        }
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Authorization", "Token $TOKEN")
        conn.setRequestProperty("Accept", "application/json")
        try {
            conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }

fun String?.withSuffix(suffix: String): String? {
    return if (this == null) null else if (this.contains("?")) "$this&$suffix" else "$this?$suffix"
}


inline fun <reified D> downloadOpenPNRR(apiType: String,
                                        crossinline onResponse: (List<D>) -> Unit) =
    runBlocking {
        val json = Json { ignoreUnknownKeys = true }

        var url: String? = "$prefixUrl$apiType".withSuffix("page=$fromPage&page_size=$page_size")
        var currentPage = fromPage
        val result = mutableListOf<D>()
        try {
            do {
                // Scarica i dati JSON
                val listJson = downloadJson(url!!, print = true)
                val resp = json.decodeFromString<ApiResponse<D>>(listJson)
                result.addAll(resp.results)
                currentPage++
                url = if (currentPage < fromPage + pageCount) resp.next else null
            } while (url != null)
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Errore durante il download: ${e.message}")
        }
        onResponse(result)
        println("Next page: $currentPage")
    }


inline fun <reified D> downloadRecursiveOpenPNRR(apiType: String,
                                                 crossinline onResponse: (List<D>) -> Unit) {
    runBlocking {
        val json = Json { ignoreUnknownKeys = true }
        var url: String? = "$prefixUrl$apiType".withSuffix("page=$fromPage&page_size=$page_size")
        var currentPage = fromPage
        val result = mutableListOf<ResponseData<D>>()
        val count = AtomicInteger((fromPage - 1) * page_size)
        val dispatcher =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2).asCoroutineDispatcher()
        try {
            do {
                // Scarica i dati JSON
                val listJson = downloadJson(url!!, print = true)
                val resp = json.decodeFromString<ApiResponse<ResponseData<D>>>(listJson)
                withContext(dispatcher) {
                    resp.results.map { s ->
                        async {
                            val dataJson = downloadJson(s.url, "${count.incrementAndGet()}/${resp.count}")
                            s.data = json.decodeFromString<D>(dataJson)
                        }
                    }.awaitAll()
                }
                result.addAll(resp.results)
                currentPage++
                url = if (currentPage < fromPage + pageCount) resp.next else null
            } while (url != null)
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Errore durante il download: ${e.message}")
        } finally {
            dispatcher.close()
        }
        println("✅ Download completed")
        onResponse(result.map { it.data!! })
        println("Next page: $currentPage")
    }
}

