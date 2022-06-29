package org.cyntho.fh.kotlin.kartoffelpuffer.net

import androidx.lifecycle.LifecycleCoroutineScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class NetManager(lifecycleCoroutineScope: LifecycleCoroutineScope) {

    private val host: String = "10.0.2.2"
    private val port: Int = 8080

    private val scope: LifecycleCoroutineScope = lifecycleCoroutineScope
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation){
            json()
        }
    }

    public suspend fun register(id: String): NetPacket?{
        try {
            var answer: NetPacket? = null
            val response: HttpResponse = client.post("http://10.0.2.2:8080/register"){
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(NetPacket(
                    System.currentTimeMillis(),
                    "",
                    0,
                    id
                ))
            }
            return response.body<NetPacket>()
        } catch (any: java.lang.Exception){
            println("NetManager.requestTokenFromServer() --> Unable to connect to backend!")
        }
        return null
    }
}