package org.cyntho.fh.kotlin.kartoffelpuffer

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
import kotlinx.coroutines.*
import org.cyntho.fh.kotlin.kartoffelpuffer.data.Guest
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.ActivityMainBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import java.util.*

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
        if (cfg == null){
            println("Config is null!")
        }
        val uuid: String = getAdvertiserUUID()
        val name: String = cfg.getString("cfgUserName", "Unknown")!!

        val guest = runBlocking {
            reqTokenAsync(uuid)
        }
        guest.userName = name

        println("Guest: [$guest]")

        // DEBUG END

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private suspend fun reqTokenAsync(uuid: String): Guest {
        val manager = NetManager(lifecycleScope)
        val response = manager.register(uuid)

        return(Guest(response?.userToken ?: "Unrecognized",
        response?.data ?: "Unknown"))
    }

    private fun getAdvertiserUUID(): String {
        val cfg = getPreferences(Context.MODE_PRIVATE)
        if (cfg == null){
            println("Unable to load preferences")
        } else if (cfg.getString("client_uuid", "") == "") {
            with (cfg.edit()){
                putString("client_uuid", UUID.randomUUID().toString())
                apply()
            }
        }

        return cfg.getString("client_uuid", "ERROR: Unable to generate uuid") ?: "ERROR: Cfg broken"
    }


}