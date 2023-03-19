package com.example.reservaaulas.searchRooms

import android.app.Application
import android.content.res.Resources
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.reservaaulas.R
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.entities.Room
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SearchRoomsViewModel(application: Application): AndroidViewModel(application) {

    private val database: Database = Database()

    private var _rooms = MutableLiveData<ArrayList<Room>>()
    val rooms: LiveData<ArrayList<Room>>
        get() = _rooms

    private var _flag = MutableLiveData<Boolean>()
    val flag: LiveData<Boolean>
        get() = _flag

    private var _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String>
        get() = _userEmail

    private var _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName



    private val _modality = MutableLiveData<String>()
    val modality: LiveData<String>
        get() = _modality

    private val _door = MutableLiveData<String>()
    val door: LiveData<String>
        get() = _door

    private val _usedDoors = MutableLiveData<List<String>>()
    val usedDoors: LiveData<List<String>>
        get() = _usedDoors

    private val _nSeats = MutableLiveData<Int>()
    val nSeats: LiveData<Int>
        get() = _nSeats


    private var _searchOnlyByDoorFlag = MutableLiveData<Boolean>()
    val searchOnlyByDoorFlag: LiveData<Boolean>
        get() = _searchOnlyByDoorFlag

    private var _filteringFlag = MutableLiveData<Boolean>()
    val filteringFlag: LiveData<Boolean>
        get() = _filteringFlag

    init {
        _modality.value = null
        _door.value = null
        _usedDoors.value = null
        _nSeats.value = null
        _rooms.value = null
        _searchOnlyByDoorFlag.value = false
        _filteringFlag.value = null
    }
    // Este método lanza una bandera.
    fun setFlag(){
        if(_flag.value == null){
            _flag.value = true
        } else {
            _flag.value = !_flag.value!!
        }
    }

    // Este método obtiene desde la base de datos todas las aulas disponibles.
    suspend fun getAllRooms() {
        viewModelScope.launch {
            _rooms.value = database.getAllRooms() as ArrayList<Room>
        }
    }
    //************ Getters & Setters ******************/
    fun setUserEmail(email: String){
        _userEmail.value = email
    }

    fun setUserName(userName: String){
        _userName.value = userName
    }

    fun setModality(modality: String){
        _modality.value = modality
    }

    fun setDoor(door: String){
        _door.value = door
    }

    fun setNumberOfSeats(nSeats: Int){
        _nSeats.value = nSeats
    }
    //**************************************/

    // Este método obtiene todas las puertas disponibles.
    suspend fun getUsedDoors(){
        _usedDoors.value = database.getUsedDoors()
    }

    // Este método lanza una bandera para buscar solo por puerta.
    fun searchOnlyByDoorFlag(){
        _searchOnlyByDoorFlag.value = _door.value != "All"
    }

    // Este método lanza una bandera para filtrar las aulas.
    fun filteringFlag(){
        _filteringFlag.value = _filteringFlag.value == null
    }


    // Este método sirve para filtrar las aulas.
    fun filterRooms(){
        if(_door.value != "All"){
            viewModelScope.launch {
                _rooms.value = database.getRoomByDoor(_door.value.toString()) as ArrayList<Room>
            }
        } else {
            viewModelScope.launch {
                _rooms.value = database.getRoomsByModalityAndNumOfSeats(_modality.value.toString(),_nSeats.value!!.toInt()) as ArrayList<Room>
            }
        }
    }

}