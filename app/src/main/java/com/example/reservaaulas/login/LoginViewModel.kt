package com.example.reservaaulas.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.entities.User
import kotlinx.coroutines.launch

class LoginViewModel(application: Application): AndroidViewModel(application) {

    private val database: Database = Database()

    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    init {
        _email.value = ""
        _password.value = ""
        _user.value = null
    }


    // Setter para insertar datos de usuario
    fun insertData(email: String, password: String){
        _email.value = email
        _password.value = password
    }

    // Función suspendida para obtener usuario de la base de datos.
    suspend fun getUserFromDB() {
         _user.value = database.getUserByEmail(_email.value.toString())
    }

    // Este método comprueba que el email del usuario coincide con el email insertado
    fun checkEmail(): Boolean {
        var correct = false
        if(_user.value!!.email == _email.value){
            correct = true
        }
        return correct
    }

    // Este método comprueba que la contraseña del usuario obtenido de la base de datos coincide con la insertada.
    fun checkPassword(): Boolean {
        var correct = false
        var result = BCrypt.verifyer().verify(_password.value.toString().toCharArray(),_user.value!!.password)
        if(result.verified){
            correct = true
        }
        return correct
    }

    // Este método Comprueba
    fun isAdmin(): Boolean {
        return _user.value!!.admin
    }

}