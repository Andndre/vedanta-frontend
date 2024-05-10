package com.example.brahmacarlearning

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.brahmacarlearning.data.local.pref.SessionPreferences
import com.example.brahmacarlearning.di.Injection
import com.example.brahmacarlearning.repository.Repository
import com.example.brahmacarlearning.view.user.login.LoginViewModel
import com.example.brahmacarlearning.view.user.chat.ChatViewModel
import com.example.brahmacarlearning.view.user.detail.DetailViewModel
import com.example.brahmacarlearning.view.user.main.MainViewModel

class ViewModelFactory(private val repository: Repository, private val sessionPrefs: SessionPreferences ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(repository, sessionPrefs) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }


            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context), Injection.provideSessionPreferences(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}