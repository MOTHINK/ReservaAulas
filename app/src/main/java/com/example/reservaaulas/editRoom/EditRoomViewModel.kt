package com.example.reservaaulas.editRoom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.entities.Room
import kotlinx.coroutines.launch

class EditRoomViewModel(application: Application): AndroidViewModel(application) {
    private val database: Database = Database()

    private var _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private var _roomToEdit = MutableLiveData<String>()
    val roomToEdit: LiveData<String>
        get() = _roomToEdit

    private var _rooms = MutableLiveData<ArrayList<Room>>()
    val rooms: LiveData<ArrayList<Room>>
        get() = _rooms

    private var _editCertainRoomFlag = MutableLiveData<Boolean>()
    val editCertainRoomFlag: LiveData<Boolean>
        get() = _editCertainRoomFlag


    init {
        _editCertainRoomFlag.value = null
        _rooms.value = null
    }

    //**************** Getter & Setter ****************

    fun setUserEmail(email: String){
        _email.value = email
    }

    fun setRoomToEdit(door: String){
        _roomToEdit.value = door
    }

    // ******************************************

    // Este método lanza esta bandera.
    fun editCertainRoomFlag(){
        if(_editCertainRoomFlag.value == null){
            _editCertainRoomFlag.value = true
        } else {
            _editCertainRoomFlag.value = !_editCertainRoomFlag.value!!
        }
    }

    // Este método obtiene de la base de datos todas las aulas disponibles.
    suspend fun getAllRooms(){
        viewModelScope.launch {
            _rooms.value = database.getAllRooms() as ArrayList<Room>
        }
    }

}