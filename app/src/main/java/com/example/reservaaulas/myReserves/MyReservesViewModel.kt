package com.example.reservaaulas.myReserves

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.entities.Reserve
import com.example.reservaaulas.entities.Room
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoField

class MyReservesViewModel(application: Application): AndroidViewModel(application) {
    private val database: Database = Database()

    private var _rooms = MutableLiveData<ArrayList<Room>>()
    val rooms: LiveData<ArrayList<Room>>
        get() = _rooms

    private var _reserves = MutableLiveData<ArrayList<Reserve>>()
    val reserves: LiveData<ArrayList<Reserve>>
        get() = _reserves

    private var _flag = MutableLiveData<Boolean>()
    val flag: LiveData<Boolean>
        get() = _flag

    private var _leavingFlag = MutableLiveData<Boolean>()
    val leavingFlag: LiveData<Boolean>
        get() = _leavingFlag

    private var _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String>
        get() = _userEmail

    private var _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _uriImage = MutableLiveData<Uri>()
    val uriImage: LiveData<Uri>
        get() = _uriImage



    init {
        _userEmail.value = null
        _flag.value = null
    }




    fun setUserEmail(email: String){
        _userEmail.value = email
    }

    fun setUserName(userName: String){
        _userName.value = userName
    }

    fun setUriImage(uri: Uri){
        _uriImage.value = uri
    }

    suspend fun getUserImage(email: String): StorageReference {
        return database.getImageByEmail(email)
    }

    fun setFlag(){
        if(_flag.value == null){
            _flag.value = true
        } else {
            _flag.value = !_flag.value!!
        }
    }

    fun setLeavingFlag(){
        if(_leavingFlag.value == null){
            _leavingFlag.value = true
        } else {
            _leavingFlag.value = !_leavingFlag.value!!
        }
    }



    suspend fun getAllRooms() {
        viewModelScope.launch {
            _rooms.value = database.getAllRooms() as ArrayList<Room>
        }
    }

    fun getReservedRoomsByTheUser(){

        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val currHour = current.get(ChronoField.HOUR_OF_DAY)
        val currDay = current.get(ChronoField.DAY_OF_MONTH)
        val currMonth = current.get(ChronoField.MONTH_OF_YEAR)
        val currYear = current.get(ChronoField.YEAR)

        viewModelScope.launch {
            var reserveList = mutableListOf<Reserve>()
            var roomList = mutableListOf<Room>()

            for(reserve in database.getUserReserves(_userEmail.value.toString()) as ArrayList<Reserve>){
                if(reserve.day >= currDay && reserve.month >= currMonth && reserve.year >= currYear){
                    var r = database.getRoomByDoor(reserve.door.toString())
                    roomList.add(r[0])
                    reserveList.add(reserve)
                }
            }
            _reserves.value = reserveList as ArrayList<Reserve>
            _rooms.value = roomList as ArrayList<Room>
        }
    }

    fun deleteUserReserve(reserveId: String){
        database.deleteReserve(reserveId)
    }


}