package com.example.reservaaulas.myReserves

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reservaaulas.searchRooms.SearchRoomsViewModel
import java.lang.IllegalArgumentException

class MyReserversViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MyReservesViewModel::class.java)){
            return MyReservesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}