package com.example.reservaaulas.admin

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reservaaulas.database.Database
import com.google.firebase.storage.StorageReference

class AdminViewModel(application: Application): AndroidViewModel(application) {

    private val database: Database = Database()

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _uriImage = MutableLiveData<Uri>()
    val uriImage: LiveData<Uri>
        get() = _uriImage

    private val _flag = MutableLiveData<Boolean>()
    val flag: LiveData<Boolean>
        get() = _flag


    init {
        _uriImage.value = null
        _flag.value = null
    }

    // Setter para insertar email
    fun setUserEmail(email: String){
        _email.value = email
    }

    // Setter para insertar imagen como URI
    fun setUriImage(uri: Uri){
        _uriImage.value = uri
    }

    // método para modificar el valor del flag para lanzar el observador.
    fun setFlag(){
        if(_flag.value == null){
            _flag.value = true
        } else {
            _flag.value = !_flag.value!!
        }
    }

    // Función suspendida para obtener la imagen desde la base de datos.
    suspend fun getUserImage(email: String): StorageReference {
        return database.getImageByEmail(email)
    }

}