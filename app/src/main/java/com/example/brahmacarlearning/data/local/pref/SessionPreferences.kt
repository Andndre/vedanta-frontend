package com.example.brahmacarlearning.data.local.pref

import android.content.Context

class SessionPreferences(private val context: Context) {

    fun saveSessionId(sessionId: String) {
        // Dapatkan SharedPreferences menggunakan konteks yang diberikan
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("sessionId", sessionId)
            apply()
        }
    }

    fun getSessionId(): String? {
        // Dapatkan SharedPreferences menggunakan konteks yang diberikan
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPref.getString("sessionId", null)
    }
}
