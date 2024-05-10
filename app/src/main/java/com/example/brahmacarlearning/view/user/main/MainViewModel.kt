package com.example.brahmacarlearning.view.user.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.brahmacarlearning.data.local.pref.UserModel
import com.example.brahmacarlearning.repository.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

//    fun getGita() = repository.getGita()

    val getBabPaging = repository.getBabPaging().cachedIn(viewModelScope)

}