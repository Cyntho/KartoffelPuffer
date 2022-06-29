package org.cyntho.fh.kotlin.kartoffelpuffer.data

abstract class User(userToken: String, userName: String) {

    var userToken: String = userToken
    var userName: String = userName

    abstract fun login(): Boolean
    abstract fun logout()

}