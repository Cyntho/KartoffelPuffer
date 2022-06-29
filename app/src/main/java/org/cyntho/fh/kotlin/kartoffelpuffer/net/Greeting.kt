package org.cyntho.fh.kotlin.kartoffelpuffer.net

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class Greeting {

    private val client = HttpClient()
    suspend fun getData(): String {
        val response = client.get("http://10.0.2.2:8080/greeting")
        return response.bodyAsText()
    }
}