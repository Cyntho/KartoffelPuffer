package org.cyntho.fh.kotlin.kartoffelpuffer

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import org.cyntho.fh.kotlin.kartoffelpuffer.data.Guest
import org.cyntho.fh.kotlin.kartoffelpuffer.data.User
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.ActivityMainBinding
import java.util.*
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

        // do the setup
        val user = setup(this)
        if (user == null){
            println("Error occurred during setup. Unable to register user token")
            exitProcess(1)
        } else {
            println("User [${user.userToken}] logging in...")

        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setup(activity: Activity): User?{

        val cfg = activity.getPreferences(Context.MODE_PRIVATE) ?: return null
        var token = cfg.getString(getString(R.string.cfgUserToken), "")

        if (token.equals("")){
            // Generate user token
            with (cfg.edit()){
                token = UUID.randomUUID().toString()
                putString(getString(R.string.cfgUserToken), token)
            }
        }

        // ToDo: Query server to verify whether user is admin or guest. return guest for now
        return Guest(token!!, cfg.getString(getString(R.string.cfgUserName), "")!!)
    }
}