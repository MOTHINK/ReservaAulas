package com.example.reservaaulas.register

import android.app.Application
import android.net.Uri
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.entities.User
import com.example.reservaaulas.utils.Utils
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.*

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val database: Database = Database()

    private val _imageName = MutableLiveData<String>()
    val imageName: LiveData<String>
        get() = _imageName

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri>
        get() = _imageUri

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String>
        get() = _confirmPassword

    init {
        _username.value = ""
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
        _imageName.value = "None"
    }

    // Setter para insertar los datos del usuario
    fun insertData(username: String, email: String, password: String, confirmPassword: String){
        _username.value = username
        _email.value = email
        _password.value = password
        _confirmPassword.value = confirmPassword
    }

    // Seter para insertar la uri de la imagen
    fun insertImageUr(imageUri: Uri){
        _imageUri.value = imageUri
    }

    // Este método comprueba que los campos no estén vacios.
    fun checkEmptyTextViews(): String {
        var msg = "correct"
        if(_username.value!!.isEmpty() || _email.value!!.isEmpty() || _password.value!!.isEmpty() || _confirmPassword.value!!.isEmpty()) {
            msg = "Error: There are empty fields"
        }
        return msg
    }

    // Este método comprueba que el email es el corporativo
    fun checkCorporativeEmail(): String {
        var msg = "Error: Wrong corporate email"
        if(_email.value!!.contains("@")){
            if(_email.value!!.split("@")[1] == "iesmiravent.es"){
                msg = "correct"
            }
        }
        return msg
    }

    // Este método sirve para comprobar que la verificación de la contraseña coincide.
    fun confirmPassword(): String{
        var msg = "Error: Mismatch Password and Confirm password"
        if(_password.value == _confirmPassword.value){
            msg = "correct"
        }
        return msg
    }

    // Este método genera un id aleatorio para la imagen.
    private fun randomImageName(){
        _imageName.value = UUID.randomUUID().toString()
    }

    // Este método inserta el usuario.
    fun insertUserToFirebaseDatabase() {
        randomImageName()
        var bcryptPassword = BCrypt.withDefaults().hashToString(12,_password.value!!.toCharArray())
        val user = User(_username.value!!,_email.value!!,bcryptPassword,_imageName.value!!)
        viewModelScope.launch {
            database.saveUserIntoDatabase(user)
            uploadImageToFirebaseStorage()
        }
    }

    // Este método para insertar la imagen en la base de datos.
    private fun uploadImageToFirebaseStorage(){
        if(_imageUri.value != null){
            // Saving image into database
            database.saveImageIntoDatabase(_imageName.value!!,_imageUri.value!!)
        }
    }

    // Este método comrpueba que si el email insertado existe previamente
    suspend fun checkIfUserExists(): Boolean {
        var exists = false
        val user = database.getUserByEmail(_email.value.toString())
        if(user!!.email == _email.value.toString()){
            exists = true
        }
        return exists
    }
}