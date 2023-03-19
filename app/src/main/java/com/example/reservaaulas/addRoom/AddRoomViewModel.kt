package com.example.reservaaulas.addRoom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.entities.PermanentReserve
import com.example.reservaaulas.entities.Room
import kotlinx.coroutines.launch

class AddRoomViewModel(application: Application): AndroidViewModel(application) {

    private val database: Database = Database()

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

    private val _hour = MutableLiveData<String>()
    val hour: LiveData<String>
        get() = _hour

    private val _weekday = MutableLiveData<String>()
    val weekday: LiveData<String>
        get() = _weekday

    private val _flag = MutableLiveData<Boolean>()
    val flag: LiveData<Boolean>
        get() = _flag

    private val _permanentReserveHourList = MutableLiveData<MutableList<PermanentReserve>>()
    val permanentReserveHourList: LiveData<MutableList<PermanentReserve>>
        get() = _permanentReserveHourList




    init {
        _modality.value = null
        _door.value = null
        _availableDoors.value = null
        _nSeats.value = null
        _permanentReserveHourList.value = mutableListOf()
        _flag.value = null
    }

    // Este método lanza el flag
    fun setFlag(){
        if(_flag.value == null){
            _flag.value = true
        } else {
            _flag.value = !_flag.value!!
        }
    }
    // Setter para añadir el objeto Permanent Reserve en su lista pertinente
    fun addPermanentReserveHourToList(prh: PermanentReserve){
        _permanentReserveHourList.value!!.add(prh)
    }

    // Este método Borra el último elemento de la lista permanentReserveHourList
    fun removeLastPermanentReserveHourFromList(): Boolean{
        var exists = false
        if(_permanentReserveHourList.value!!.isNotEmpty()){
            exists = true
            _permanentReserveHourList.value!!.removeLast()
        }
        return exists
    }

    // Este método añade el valor de puerta en su variable pertinente
    fun addDoorToPermanentReserveHourList(){
        for(prh in _permanentReserveHourList.value!!){
            prh.door = _door.value.toString()
        }
    }

    // Este Sirve para guarda las reservas permanentes en la base de datos.
    fun savePermanentReserveHourInDB(){
        if(_permanentReserveHourList.value!!.isNotEmpty()){
            database.savePermanentReserveHour(_permanentReserveHourList.value!!)
        }
    }


    //****************** Getters & Setter ************************/
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

    fun setWeekday(weekday: String){
        _weekday.value = weekday
    }

    fun setWeekdayNull(){
        _weekday.value = null
    }

    fun setHour(hour:String){
        _hour.value = hour
    }

    fun setHourNull(){
        _hour.value = null
    }

    fun setAvailableDoors(availabeDoors: List<String>){
        _availableDoors.value = availabeDoors
    }
    //****************** Fin ************************/

    // Este metodo comprueba que todas la opciones se ha seleccionado.
    fun checkSelectedOptions(): Boolean {
        var correct = false
        if(_modality.value != null && _door.value != null && _nSeats.value != null){
            correct = true
        }
        return correct
    }

    // Este método sirve para guarda el aula en la base de datos.
    fun saveRoomInDatabase(){
        val room = Room(_modality.value.toString(),door.value.toString(), nSeats.value!!.toInt())
        database.saveRoom(room)
        database.designDoor(_door.value.toString())
        viewModelScope.launch {
            _availableDoors.value = database.getAvailableDoors()
        }
        addDoorToPermanentReserveHourList()
        database.savePermanentReserveHour(_permanentReserveHourList.value!!)
    }

    // Este método sirve para obtener las puertas disponibles.
    suspend fun getAvailableDoors(){
        _availableDoors.value = database.getAvailableDoors()
    }

    // Este método sirve para comprobar si la puerta existe en la base de datos.
    suspend fun checkDoorExistsInDB(door: String): Boolean{
        return database.checkRoomExists(door)
    }


}