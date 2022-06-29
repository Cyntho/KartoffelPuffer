package org.cyntho.fh.kotlin.kartoffelpuffer

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.cyntho.fh.kotlin.kartoffelpuffer.data.Guest
import org.cyntho.fh.kotlin.kartoffelpuffer.data.User
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.ActivityMainBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.Greeting
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_settings, R.id.navigation_home, R.id.navigation_reservations
            )
        )

        var actionBar: ActionBar? = supportActionBar
        if (actionBar != null)
        {
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setIcon(R.mipmap.ic_launcher)
        }

        val cfg = getPreferences(Context.MODE_PRIVATE)
        val uuid: String = cfg?.getString("client_uuid", UUID.randomUUID().toString())!!

        /*
        var guest: Guest? = null
        lifecycleScope.launch {
             guest = reqTokenAsync(uuid)
        }.also {
            println("Also: $guest")
        }
        */
        var guest = runBlocking {
            reqTokenAsync(uuid)
        }

        println("Guest: [$guest]")

        // DEBUG END

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private suspend fun reqTokenAsync(uuid: String): Guest {
        val manager = NetManager(lifecycleScope)
        val response = manager.requestTokenFromServer(uuid)

        return(Guest(response?.userToken ?: "Unrecognized", "to do: get name"))
    }


}