package com.example.reservaaulas.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.reservaaulas.R
//import com.example.reservaaulas.databinding.FragmentLoginBinding
import com.example.reservaaulas.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*

class RegisterFragment : Fragment() {

    // Creamos tres variables donde almacenaremos tanto los binding, como el viewmodel y su factory, y la uri
    private lateinit var viewModel: RegisterViewModel
    private lateinit var viewModelFactory: RegisterViewModelFactory
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var imageUri: Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Instanciamos el binding para poder acceder a los elementos del diseño gráfico
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register, container, false)

        // Creamos el intent de Action Pick
        val intent = Intent(Intent.ACTION_PICK)

        // Instanciamos el viewmodel y su factory
        val application = requireNotNull(this.activity).application
        viewModelFactory = RegisterViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(RegisterViewModel::class.java)

        // Enlazamos el binding al viewmodel
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Cuando pulsamos sobre la imagen de user, nos abre la carpeta donde guardamos las imagenes del móvil.
        binding.imageView2.setOnClickListener {
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        /*Este botón registra el usuario, para ello.
            1. Obtiene todos los datos insertados
            2. En un hilo buscamos si el email insertado existe en la base de datos.
            3. Si existe nos decirá que ese email ya está en uso.
            4. Comprobamos que los campos no estén vacios.
            5. Comprobar que la contraseña es correcta.
            6. si se cumplen los puntos 3,4,5 pues nos crea un nuevo usuario, sino pues nos indica los errores que hemos generado.
        */
        binding.registerButton.setOnClickListener {
            // Insert data into ViewModel LiveData
            viewModel.insertData(binding.nameEditText.text.toString(), binding.emailEditText.text.toString(),
                                 binding.passwordEditText.text.toString(), binding.repeatPasswordEditText.text.toString())
            // Check if there are errors
            lifecycleScope.launch {
                if(confirmUserData(it,binding)){
                    if(!viewModel.checkIfUserExists()){
                        viewModel.insertUserToFirebaseDatabase()
                        Navigation.findNavController(it).navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment("Successfully registered"))
                        showSnackBar(it,"Successfully registered",ResourcesCompat.getColor(resources, R.color.orange, null))
                    } else {
                        showSnackBar(it,"The given email is already in use",ResourcesCompat.getColor(resources, R.color.red, null))
                    }
                } else {
                    println("Datos incorrectos")
                }
            }
        }

        // Este botón es para volver al fragmento de login
        binding.backButton2.setOnClickListener {
            Navigation.findNavController(it).navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment("Successfully registered"))
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    // Este método obtiene la uri de la imagen seleccionada y la inserta en el viewModel
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            println("Foto seleccionada")

            imageUri = data.data!!
            viewModel.insertImageUr(data.data!!)
            // Set the selected image to imageView
            binding.circleImage.setImageURI(imageUri)
        }
    }

    // Este método muestra un snackbar
    private fun showSnackBar(view: View,message: String,color: Int) {
        val err = message
        Snackbar.make(view,err, Snackbar.LENGTH_LONG)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
            .setDuration(2000)
            .setBackgroundTint(color) // Color.parseColor("#006400")
            .show()
    }

    // Este método sirve para comproba que los datos insertados sean correctos.
    private fun confirmUserData(it: View,binding: FragmentRegisterBinding): Boolean{
        var correct = true
        val errors = arrayOf(viewModel.checkEmptyTextViews(),viewModel.checkCorporativeEmail(),viewModel.confirmPassword())
        for(error in errors){
            if(error != "correct"){
                correct = false
                binding.passwordEditText.text.clear()
                binding.repeatPasswordEditText.text.clear()
                showSnackBar(it,error,ResourcesCompat.getColor(resources, R.color.red, null))
                break
            }
        }
        return correct
    }
}