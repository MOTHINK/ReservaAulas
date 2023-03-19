package com.example.reservaaulas.reserveRoom

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reservaaulas.searchRooms.SearchRoomsViewModel
import java.lang.IllegalArgumentException

class ReserveRoomViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ReserveRoomViewModel::class.java)){
            return ReserveRoomViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}