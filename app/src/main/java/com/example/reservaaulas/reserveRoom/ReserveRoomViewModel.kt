package com.example.reservaaulas.reserveRoom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.entities.PermanentReserve
import com.example.reservaaulas.entities.Reserve
import com.example.reservaaulas.entities.Room
import kotlinx.coroutines.launch

class ReserveRoomViewModel(application: Application): AndroidViewModel(application) {

    private val database: Database = Database()

    private var _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String>
        get() = _userEmail

    private val _door = MutableLiveData<String>()
    val door: LiveData<String>
        get() = _door

    private var _room = MutableLiveData<ArrayList<Room>>()
    val room: LiveData<ArrayList<Room>>
        get() = _room

    private var _day = MutableLiveData<Int>()
    val day: LiveData<Int>
        get() = _day

    private var _weekday = MutableLiveData<Int>()
    val weekday: LiveData<Int>
        get() = _weekday

    private var _month = MutableLiveData<Int>()
    val month: LiveData<Int>
        get() = _month

    private var _year = MutableLiveData<Int>()
    val year: LiveData<Int>
        get() = _year

    private var _flag = MutableLiveData<Boolean>()
    val flag: LiveData<Boolean>
        get() = _flag

    private var _reserves = MutableLiveData<ArrayList<Reserve>>()
    val reserves: LiveData<ArrayList<Reserve>>
        get() = _reserves


    init {
        _door.value = null
        _userEmail.value = null
        _day.value = 0
        _month.value = 0
        _year.value = 0
        _flag.value = null
        _weekday.value = null
    }

    //************* Getters & Setters ***************/
    fun setDay(day: Int){
        _day.value = day
    }

    fun setWeekday(weekday: Int){
        _weekday.value = weekday
    }

    fun setMonth(month: Int){
        _month.value = month
    }

    fun setYear(year: Int){
        _year.value = year
    }


    fun setUserEmail(email: String){
        _userEmail.value = email
    }

    fun setDoor(door: String){
        _door.value = door
    }
    //**************************************/

    // Este método lanza una bandera.
    fun setFlag(){
        if(_flag.value == null){
            _flag.value = true
        } else {
            _flag.value = !_flag.value!!
        }
    }

    // Esta función suspendida obtiene el aula según la puerta.
    suspend fun getRoomByDoor(){
        _room.value = database.getRoomByDoor(_door.value.toString()) as ArrayList<Room>
    }

    // Este método guarda la reserva en la base de datos, y lanza una bandera para mostrar las modificaciones en la vista.
    fun reserveAnHour(hour: Int){
        var id = hour.toString() + _day.value.toString() + _month.value.toString() + _year.value.toString() + _userEmail.value.toString() + _door.value.toString()
        var reserve = Reserve(id,_door.value.toString(),_userEmail.value.toString(),_day.value!!.toInt(),_month.value!!.toInt(),_year.value!!.toInt(),hour)
        database.saveReserve(reserve)
        setFlag()
    }

    // Este método obtiene todas las reservas realizadas.
    suspend fun getReservesAndFilterAvailable(): ArrayList<Reserve> {
        return database.getAllReserves(_day.value!!.toInt(),_month.value!!.toInt(),_year.value!!.toInt(),_door.value.toString()) as ArrayList<Reserve>
    }

    // Este método obtiene todas las reservas permanetes por nombre de la puerta.
    suspend fun getPermanentReservesByDoor(door: String): MutableList<PermanentReserve> {
        return database.getPermanentReserveHourByDoor(door)
    }

}