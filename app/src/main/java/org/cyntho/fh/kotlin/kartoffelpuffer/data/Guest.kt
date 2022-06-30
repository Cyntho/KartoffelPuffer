package org.cyntho.fh.kotlin.kartoffelpuffer.data

class Guest(userToken: String, userName: String) : User(userToken, userName) {

    private fun login(token: String): Boolean{

        return false
    }

    override fun login(): Boolean {
        return login(super.userToken)
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    fun getToken(): String {
        return userToken;
    }

    override fun toString(): String {
        return "Guest::Class -> [userToken = $userToken], [userName = $userName]"
    }
}