package com.example.reservaaulas.login

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.reservaaulas.R
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.databinding.FragmentLoginBinding
import com.example.reservaaulas.entities.User
import com.example.reservaaulas.register.RegisterFragmentDirections
import com.example.reservaaulas.register.RegisterViewModel
import com.example.reservaaulas.register.RegisterViewModelFactory
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class LoginFragment : Fragment() {

    // Creamos tres variables donde almacenaremos tanto los binding, como el viewmodel y su factory

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var viewModelFactory: LoginViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Instanciamos el binding para poder acceder a los elementos del diseño gráfico
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false)


        // Obtenemos los argumentos que se pasan por el action
        val args = LoginFragmentArgs.fromBundle(requireArguments())
        val message = args.registered

        // Instanciamos el viewmodel y su factory
        val application = requireNotNull(this.activity).application
        viewModelFactory = LoginViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(LoginViewModel::class.java)

        // Enlazamos el binding al viewmodel
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        /*
        * Al hacer click sobre el botón login lo que hace es:
        *   1. primero comprueba que los campos no estén vacíos.
        *   2. Obtener el usuario que queremos buscar de la base de datos
        *   3. verificamos que el usuario existe, y que la contraseña coincide.
        *   4. Si el usuario existe y la contraseña insertada coincide,
        *   5. Comprueba que es un admin on un usuaario normal.
        *   6. Si existe nos redirecciona a las siguiente vista correspondiente,
        *      sino, pues nos muestra su respectivo error.
        * */
        binding.loginButton.setOnClickListener {
            if(!checkEmptyFields()){
                viewModel.insertData(binding.emailEditText.text.toString(),binding.passwordEditText.text.toString())
                lifecycleScope.launch{
                    viewModel.getUserFromDB()
                    if(verifyUser()){
                        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
                            if(user.admin){
                                showSnackBar(it,"Successfully Logged in, as an Admin",ResourcesCompat.getColor(resources, R.color.green, null))
                                Navigation.findNavController(it).navigate(LoginFragmentDirections.actionLoginFragmentToAdminFragment(user.email,user.name))
                            } else {
                                showSnackBar(it,"Successfully Logged in, as an User",ResourcesCompat.getColor(resources, R.color.green, null))
                                Navigation.findNavController(it).navigate(LoginFragmentDirections.actionLoginFragmentToUserFragment(user.email,user.name))
                            }
                        })
                    } else {
                        showSnackBar(it,"Email or password are incorrects",ResourcesCompat.getColor(resources, R.color.red, null))
                        binding.passwordEditText.setText("")
                    }
                }
            } else {
                showSnackBar(it,"Empty Fields",ResourcesCompat.getColor(resources, R.color.red, null))
            }
        }

        //Este botón nos redirecciona al fragmento de registrar usuario
        binding.registerButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        // Inflate the layout for this fragment
        return binding.root
    }



    //Este método al llamarse nos muestra un snackbar con el mensaje que le insertamos y el color identificador.
    private fun showSnackBar(view: View,message: String,color: Int) {
        val err = message
        Snackbar.make(view,err, Snackbar.LENGTH_LONG)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
            .setDuration(2000)
            .setBackgroundTint(color) // Color.parseColor("#006400")
            .show()
    }

    // Este método verifica si los campos están vacíos o no.
    private fun checkEmptyFields(): Boolean {
        var empty = false
        if(binding.emailEditText.text.toString().isEmpty() || binding.passwordEditText.text.toString().isEmpty()){
            empty = true
        }
        return empty
    }

    // Este método verifica si el usuario existe y si es así la si la contraseña coincide.
    private fun verifyUser(): Boolean {
        var allCorrect = false

        if(!viewModel.user.value!!.password.isEmpty()){
            val correctEmail = viewModel.checkEmail()
            val correctPassword = viewModel.checkPassword()

            if(correctEmail && correctPassword){
                allCorrect = true
            }
        }

        return allCorrect
    }

}