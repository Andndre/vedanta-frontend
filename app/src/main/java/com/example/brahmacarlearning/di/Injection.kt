package com.example.brahmacarlearning.di

import android.content.Context
import com.example.brahmacarlearning.data.local.database.BabDatabase
import com.example.brahmacarlearning.data.local.pref.SessionPreferences
import com.example.brahmacarlearning.data.local.pref.UserPreference
import com.example.brahmacarlearning.data.local.pref.dataStore
import com.example.brahmacarlearning.data.remote.api.ApiConfig
import com.example.brahmacarlearning.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val database = BabDatabase.getDatabase(context)
        return Repository.getInstance(pref, apiService, database)
    }

    fun provideSessionPreferences(context: Context): SessionPreferences {
        return SessionPreferences(context)
    }
}