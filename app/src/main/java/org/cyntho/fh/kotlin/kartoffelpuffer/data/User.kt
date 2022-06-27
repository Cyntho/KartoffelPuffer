package org.cyntho.fh.kotlin.kartoffelpuffer.data

abstract class User {


    val userToken: String = ""

    abstract fun login(): Boolean
    abstract fun logout()

}