package com.example.reservaaulas.admin

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reservaaulas.login.LoginViewModel
import java.lang.IllegalArgumentException

class AdminViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AdminViewModel::class.java)){
            return AdminViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}