package org.cyntho.fh.kotlin.kartoffelpuffer.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.*
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.data.Dish
import org.cyntho.fh.kotlin.kartoffelpuffer.data.ReservationHolder
import org.cyntho.fh.kotlin.kartoffelpuffer.net.AllergyWrapper
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket
import java.util.*

class KartoffelApp : Application() {

    private var _userName: String  = "Uninitialized"
    private var _userUUID: String  = "Uninitialized"
    private var _userToken: String = "Uninitialized"
    private var _isAdmin: Boolean = false
    private var _adminViewActive: Boolean = false

    // Global runtime
    private var _currentReservationAttempt: ReservationHolder? = null
    private var _allergyList: MutableList<AllergyWrapper> = mutableListOf()
    private var _dishList: MutableList<Dish> = mutableListOf()
    private var _dishMap: MutableMap<Int, Dish> = mutableMapOf()
    private var _currentLayoutID: Int = -1


    public fun getUserName() : String{ return _userName }
    public fun getUserToken() : String { return _userToken }
    public fun isAdmin(): Boolean { return _isAdmin }
    public fun displayAdminView(): Boolean { return _adminViewActive }

    public fun setUserName(value: String) { _userName = value }
    public fun setUserUUID(value: String) { _userUUID = value }
    public fun setUserToken(value: String) { _userToken = value }
    public fun setAdmin(value: Boolean) { _isAdmin = value }
    public fun setAdminView(value: Boolean) { _adminViewActive = value}

    // Reservation
    public fun setCurrentReservation(res: ReservationHolder){ _currentReservationAttempt = res}
    public fun getCurrentReservation(): ReservationHolder? { return _currentReservationAttempt }

    public fun resetCurrentReservation(){ _currentReservationAttempt = null }

    public fun setCurrentLayoutID(id: Int) {_currentLayoutID = id }
    public fun getCurrentLayoutID(): Int { return _currentLayoutID }


    // Allergies
    public fun setAllergyList(list: MutableList<AllergyWrapper>) { _allergyList = list}
    public fun getAllergyList(): MutableList<AllergyWrapper> { return _allergyList}

    // Dishes
    public fun setDishList(list: MutableList<Dish>) {
        _dishList = list
        _dishMap = mutableMapOf()
        for (entry in list){
            _dishMap[entry.dishId] = entry
        }
    }
    public fun getDishList(): MutableList<Dish> { return _dishList }

    public fun getDishById(id: Int): Dish? { return _dishMap[id]}


    public fun save(){
        try {
            val cfg = getSharedPreferences("config", Context.MODE_PRIVATE)
            if (cfg != null){
                with (cfg.edit()) {
                    putString(getString(R.string.cfgUserName), _userName)
                    putBoolean(getString(R.string.cfgAdminViewActive), _adminViewActive)
                    apply()
                }
            } else {
                println("Unable to save App data")
            }
        } catch (any: java.lang.Exception){
            any.printStackTrace()
        }
    }

    public fun load(){
        try {
            val cfg = getSharedPreferences("config", Context.MODE_PRIVATE)
            if (cfg != null){
                _userName = cfg.getString(getString(R.string.cfgUserName), "New Guest")!!
                _adminViewActive = cfg.getBoolean(getString(R.string.cfgAdminViewActive), false)
            }
        } catch (any: Exception){
            any.printStackTrace()
        }
    }


}