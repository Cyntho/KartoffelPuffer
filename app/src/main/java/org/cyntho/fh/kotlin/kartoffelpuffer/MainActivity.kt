package org.cyntho.fh.kotlin.kartoffelpuffer

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.*
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
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
                R.id.navigation_settings, R.id.navigation_home, R.id.navigation_reservations, R.id.navigation_setup
            )
        )

        var actionBar: ActionBar? = supportActionBar
        if (actionBar != null)
        {
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setIcon(R.mipmap.ic_launcher)
        }

        val cfg = getSharedPreferences("config", Context.MODE_PRIVATE)

        val uuid = cfg.getString(getString(R.string.cfgUUID), UUID.randomUUID().toString()) ?: UUID.randomUUID().toString()
        val name: String = cfg.getString(getString(R.string.cfgUserName), "Unknown") ?: "Unknown"

        val app: KartoffelApp = application as KartoffelApp
        val serverResponse = runBlocking {
            NetManager().register(uuid)
        }

        app.setUserUUID(uuid)
        app.setUserName(name)
        app.setUserToken(serverResponse?.userToken ?: "ERR_CONNECTION")
        app.setAdmin(serverResponse?.data.toBoolean())
        app.setAdminView(false)
        app.debug()

        with (cfg.edit()){
            putString(getString(R.string.cfgUUID), uuid)
            apply()
        }


        // DEBUG END

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}