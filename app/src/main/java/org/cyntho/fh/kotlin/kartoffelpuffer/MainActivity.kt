package org.cyntho.fh.kotlin.kartoffelpuffer

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.GsonBuilder
import io.ktor.client.request.*
import kotlinx.coroutines.*
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.data.Dish
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.ActivityMainBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.AllergyWrapper
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket
import java.io.File
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
                R.id.navigation_settings,
                R.id.navigation_home,
                R.id.navigation_reservations,
                R.id.navigation_setup,
                R.id.navigation_reservation_details,
                R.id.navigation_reservation_confirmation,
                R.id.navigation_dish_details
            )
        )

        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null)
        {
            actionBar.setDisplayShowHomeEnabled(false)
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

        with (cfg.edit()){
            putString(getString(R.string.cfgUUID), uuid)
            apply()
        }

        getUpdateFromServer()

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


    /**
     * Update stored stuff from server
     * @return void
     */
    private fun getUpdateFromServer() {
        runBlocking {
            val response = NetManager().send("/getAllergyList", NetPacket(
                System.currentTimeMillis(),
                (application as KartoffelApp).getUserToken(),
                0,
                "")
            )

            if (response != null){
                val raw = GsonBuilder().create().fromJson(response.data, Array<AllergyWrapper>::class.java)
                val list = raw.asList().toMutableList()
                (application as KartoffelApp).setAllergyList(list)
                println("Allergies loaded.")
            } else {
                println("Unable to load Allergies")
            }
        }

        runBlocking {
            val response = NetManager().send("/getDishes", NetPacket(
                System.currentTimeMillis(),
                (application as KartoffelApp).getUserToken(),
                0,
                "")
            )

            if (response != null){
                val raw = GsonBuilder().create().fromJson(response.data, Array<Dish>::class.java)
                val list = raw.asList().toMutableList()
                (application as KartoffelApp).setDishList(list)
                println("Dishes loaded.")
            } else {
                println("Unable to load Dishes")
            }
        }
    }
}