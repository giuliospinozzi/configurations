package it.umbria.regione.openpnrr

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<R>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<R>
)

@Serializable
data class ResponseData<D>(val url: String,
                           val id: Int = url.substringAfterLast("/").toInt(),
                           var data: D? = null)
