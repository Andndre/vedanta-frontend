package com.example.brahmacarlearning.view.user.detail

import androidx.lifecycle.ViewModel
import com.example.brahmacarlearning.repository.Repository

class DetailViewModel(private  val repository: Repository) : ViewModel() {
    fun getDetailGita(bab : String) = repository.getDetailGita(bab)
}