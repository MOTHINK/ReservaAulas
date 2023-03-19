package com.example.reservaaulas.searchRooms

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reservaaulas.user.UserViewModel
import java.lang.IllegalArgumentException

class SearchRoomsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchRoomsViewModel::class.java)){
            return SearchRoomsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}