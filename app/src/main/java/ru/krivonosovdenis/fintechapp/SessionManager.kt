package ru.krivonosovdenis.fintechapp

import android.content.Context

class SessionManager(val context: Context) {
    companion object {
        private const val PREF_NAME = "TOKEN_SHARED_PREF"
        const val API_TOKEN = "API_TOKEN"
        const val PRIVATE_MODE = 0
    }

    fun storeSessionToken(token: String) {
        val pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val editor = pref.edit()
        editor.putString(API_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String {
        val pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        return pref.getString(API_TOKEN, "") ?: ""
    }

}
