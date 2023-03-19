package com.example.reservaaulas.deleteRoom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.entities.Room
import com.example.reservaaulas.entities.User
import kotlinx.coroutines.launch

class DeleteRoomViewModel(application: Application): AndroidViewModel(application) {

    private val database: Database = Database()

    private var _rooms = MutableLiveData<ArrayList<Room>>()
    val rooms: LiveData<ArrayList<Room>>
        get() = _rooms

    private var _flag = MutableLiveData<Boolean>()
    val flag: LiveData<Boolean>
        get() = _flag

    private var _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private var _delete = MutableLiveData<Boolean>()
    val delete: LiveData<Boolean>
        get() = _delete

    private var _deletedRoom = MutableLiveData<String>()
    val deletedRoom: LiveData<String>
        get() = _deletedRoom

    init {
        _delete.value = false
        _flag.value = null
        _rooms.value = null
    }

    // Este método lanza un flag
    fun deleteFlag(){
        _delete.value = !_delete.value!!
    }

    //************* Getters & Setters *************/

    fun setUserEmail(email: String){
        _email.value = email
    }

    fun setDeletedDoor(door: String){
        _deletedRoom.value = door
    }

    fun setArrayListRoom(roomArrayList: ArrayList<Room>){
        _rooms.value = roomArrayList
    }

    fun getArrayListRoom(): ArrayList<Room> {
        return _rooms.value!!
    }
    //************************/

    // Este método lanza el flag.
    fun setFlag(){
        if(_flag.value == null){
            _flag.value = true
        } else {
            _flag.value = !_flag.value!!
        }
    }


    // Esta función suspendidad devuelve todas las aulas de la base de datos.
    suspend fun getAllRooms() {
        viewModelScope.launch {
            _rooms.value = database.getAllRooms() as ArrayList<Room>
        }
    }

    // Este método borra el aula, además borra la designación a su puerta, borra las reservas que hay de este aula
    // Borra las reservas permantes de este aula.
    fun deleteRoom(door: String){
        database.deleteRoom(door)
        database.undesignRoom(door)
        database.deleteReserveByDoor(door)
        database.deletePermanentReserveHourByDoor(door)
    }
}