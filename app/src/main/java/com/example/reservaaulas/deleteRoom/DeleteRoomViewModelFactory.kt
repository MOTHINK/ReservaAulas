package com.example.reservaaulas.deleteRoom

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reservaaulas.addRoom.AddRoomViewModel
import java.lang.IllegalArgumentException

class DeleteRoomViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DeleteRoomViewModel::class.java)){
            return DeleteRoomViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}