package com.example.reservaaulas.roomToEdit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.entities.Room

class RoomToEditViewModel(application: Application): AndroidViewModel(application) {
    private val database: Database = Database()

    private var _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _modality = MutableLiveData<String>()
    val modality: LiveData<String>
        get() = _modality

    private val _door = MutableLiveData<String>()
    val door: LiveData<String>
        get() = _door

    private val _availableDoors = MutableLiveData<List<String>>()
    val availableDoors: LiveData<List<String>>
        get() = _availableDoors

    private val _nSeats = MutableLiveData<Int>()
    val nSeats: LiveData<Int>
        get() = _nSeats

    private val _confirmEditFlag = MutableLiveData<Boolean>()
    val confirmEditFlag: LiveData<Boolean>
        get() = _confirmEditFlag

    init {
        _confirmEditFlag.value = false
        _modality.value = null
        _door.value = null
        _availableDoors.value = null
        _nSeats.value = null
    }

    // Este método lanza un flag
    fun confirmEditFlag(){
        _confirmEditFlag.value = _confirmEditFlag.value == false
    }

    // **************** Setters *******************
    fun setUserEmail(email: String){
        _email.value = email
    }

    fun setModality(modality: String){
        _modality.value = modality
    }

    fun setModalityNull(){
        _modality.value = null
    }

    fun setDoor(door: String){
        _door.value = door
    }

    fun setDoorNull() {
        _door.value = null
    }

    fun setNumberOfSeats(nSeats: Int){
        _nSeats.value = nSeats
    }
    fun setNumberOfSeatsNull() {
        _nSeats.value = null
    }


    fun setAvailableDoors(availabeDoors: List<String>){
        _availableDoors.value = availabeDoors
    }

    //*****************************************************/


    // Este método sirve para comprobar las opciones seleccionadas. (En este caso no lo estamos usando)
    fun checkSelectedOptions(): Boolean {
        var correct = false
        if(_modality.value != null && _door.value != null && _nSeats.value != null){
            correct = true
        }
        return correct
    }

    // Este método obtiene las puertas dispobles de la base de datos.
    suspend fun getAvailableDoors(){
        _availableDoors.value = database.getAvailableDoors()
    }

    // Este método obtiene el aula que se quiere modificar.
    suspend fun getRoomToEdit(door: String){
        val room = database.getRoomToEdit(door)
        setModality(room.modality)
        setDoor(room.door)
        setNumberOfSeats(room.nSeats)
    }

    // Este método es para modificar el aula que hemos seleccionado.
    fun updateRoom(currDoor: String){
        if(currDoor != _door.value.toString()){
            database.undesignRoom(currDoor)
            database.deleteRoom(currDoor)
        }
        database.saveRoom(Room(_modality.value.toString(),_door.value.toString(),_nSeats.value!!.toInt()))
    }

}