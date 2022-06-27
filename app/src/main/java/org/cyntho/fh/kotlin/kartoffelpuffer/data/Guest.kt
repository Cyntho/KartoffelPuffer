package org.cyntho.fh.kotlin.kartoffelpuffer.data

class Guest(token: String, name:String) : User() {

    private fun login(token: String): Boolean{

        return false
    }

    override fun login(): Boolean {
        return login(super.userToken)
    }

    override fun logout() {
        TODO("Not yet implemented")
    }
}