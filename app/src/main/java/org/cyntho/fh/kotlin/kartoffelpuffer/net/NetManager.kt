package org.cyntho.fh.kotlin.kartoffelpuffer.net

import androidx.lifecycle.LifecycleCoroutineScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class NetManager() {

    private val host: String = "http://10.0.2.2:8080"

    private val client: HttpClient = HttpClient {
        install(ContentNegotiation){
            json()
        }
        install(HttpTimeout){
            requestTimeoutMillis = 1000
        }
    }

    public suspend fun register(id: String): NetPacket?{
        try {
            val response: HttpResponse = client.post("$host/register"){
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(NetPacket(
                    System.currentTimeMillis(),
                    "",
                    1,
                    id
                ))
            }
            return response.body<NetPacket>()
        } catch (any: java.lang.Exception){
            println("NetManager.requestTokenFromServer() --> Unable to connect to backend!")
        }
        return null
    }

    public suspend fun login(token: String, code: String): Boolean {
        try {
            val response: HttpResponse = client.post("$host/auth"){
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(NetPacket(
                    System.currentTimeMillis(),
                    token,
                    2,
                    code
                ))
            }
            val p = response.body<NetPacket>()
            return p.type == 0 && p.data == "AUTH_SUCCESSFUL"
        } catch (any: java.lang.Exception){
            println("NetManager.login() --> Unable to connect to backend!")
        }
        return false
    }

    public suspend fun send(path: String, netPacket: NetPacket): NetPacket? {
        try {
            val response: HttpResponse = client.post("$host/$path"){
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(netPacket)
            }
            return response.body<NetPacket>()
        } catch (any:java.lang.Exception){
            println("NetManager.send() --> Unable to connect to backend!")
        }
        return null
    }
}