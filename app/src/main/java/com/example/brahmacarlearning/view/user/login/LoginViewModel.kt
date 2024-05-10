package com.example.brahmacarlearning.view.user.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brahmacarlearning.data.local.pref.UserModel
import com.example.brahmacarlearning.repository.Repository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository): ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    fun saveSession(user: UserModel){
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}