package com.example.reservaaulas.roomToEdit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class RoomToEditViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RoomToEditViewModel::class.java)){
            return RoomToEditViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}