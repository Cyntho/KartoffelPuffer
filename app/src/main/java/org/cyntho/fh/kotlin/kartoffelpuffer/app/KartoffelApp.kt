package org.cyntho.fh.kotlin.kartoffelpuffer.app

import android.app.Application
import android.content.Context
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import java.util.*

class KartoffelApp : Application() {

    private var _userName: String  = "Uninitialized"
    private var _userUUID: String  = "Uninitialized"
    private var _userToken: String = "Uninitialized"
    private var _isAdmin: Boolean = false
    private var _adminViewActive: Boolean = false


    public fun getUserName() : String{ return _userName }
    public fun getUserToken() : String { return _userToken }
    public fun isAdmin(): Boolean { return _isAdmin }
    public fun displayAdminView(): Boolean { return _adminViewActive }

    public fun setUserName(value: String) { _userName = value }
    public fun setUserUUID(value: String) { _userUUID = value }
    public fun setUserToken(value: String) { _userToken = value }
    public fun setAdmin(value: Boolean) { _isAdmin = value }
    public fun setAdminView(value: Boolean) { _adminViewActive = value}

    public fun debug(){
        println("Username: [$_userName]\t token: [$_userToken]\t uuid: [$_userUUID]\t isAdmin: [$_isAdmin]\t adminView: [$_adminViewActive]")
    }

    public fun save(){
        try {
            val cfg = getSharedPreferences("config", Context.MODE_PRIVATE)
            if (cfg != null){
                with (cfg.edit()) {
                    putString(getString(R.string.cfgUserName), _userName)
                    apply()
                }
            } else {
                println("Unable to save App data")
            }
        } catch (any: java.lang.Exception){
            any.printStackTrace()
        }
    }

}