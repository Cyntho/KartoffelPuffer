package org.cyntho.fh.kotlin.kartoffelpuffer.net

import android.provider.Settings.System.getString
import android.util.JsonWriter
import androidx.lifecycle.LifecycleCoroutineScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.Socket
import java.nio.ByteBuffer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class NetManager {

    private val host: String = "10.0.2.2"
    private val port: Int = 8080

    private val scope: LifecycleCoroutineScope
    private val client: HttpClient

    constructor(lifecycleCoroutineScope: LifecycleCoroutineScope){
        scope = lifecycleCoroutineScope
        client = HttpClient {
            install(ContentNegotiation){
                json()
            }
        }
    }

    public suspend fun requestTokenFromServer(id: String): NetPacket?{
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

    suspend fun reqTokenNew(id: String): NetPacket? {

        try {
            var data: NetPacket? = null
            runBlocking {
                val response = client.post("http://10.0.2.2/register"){
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    setBody(NetPacket(
                        System.currentTimeMillis(),
                        "",
                        0,
                        id
                    ))
                }
                data = response.body<NetPacket>()
            }
            return data
        } catch (any: java.lang.Exception){
            println("NetManager error")
        }
        return null
    }
}