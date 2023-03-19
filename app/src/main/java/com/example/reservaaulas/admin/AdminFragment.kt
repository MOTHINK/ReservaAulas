package com.example.reservaaulas.admin

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.reservaaulas.R
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.databinding.FragmentAdminBinding
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.reservaaulas.myReserves.MyReservesViewModel


class AdminFragment : Fragment() {

    // Creamos tres variables donde almacenaremos tanto los binding, como el viewmodel y su factory

    private lateinit var binding: FragmentAdminBinding
    private lateinit var viewModel: AdminViewModel
    private lateinit var viewModelFactory: AdminViewModelFactory


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Instanciamos el binding para poder acceder a los elementos del diseño gráfico
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_admin, container, false)

        // Obtenemos los argumentos que se pasan por el action
        val args = AdminFragmentArgs.fromBundle(requireArguments())
        val email = args.adminEmail
        var user = args.userName

        // Instanciamos el viewmodel y su factory
        val application = requireNotNull(this.activity).application
        viewModelFactory = AdminViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(AdminViewModel::class.java)

        // Enlazamos el binding al viewmodel
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Insertamos el email en el viewmodel
        viewModel.setUserEmail(email)


        // En este ámbito abrimos un hilo el cual espera recibir la imagen y luego guardarla en el viewmodel.
        lifecycleScope.launch {
            val img = viewModel.getUserImage(email)
            img.downloadUrl.addOnSuccessListener {
                viewModel.setUriImage(it)
            }
        }

        // Este observador espera que se obtenga la imagen desde la base de datos y luego insertarla
        viewModel.uriImage.observe(viewLifecycleOwner, Observer {
            Glide.with(this).load(viewModel.uriImage.value).into(binding.circleImage)
        })

        // Insertamos el texto en que se muestra el nombre del usuario en la vista
        binding.textView.text = "Registered as: " + user

        // Este observador espera que el livedata flag se cambie, una vez cambie se Navega al fragmento de login, [Cerrar sesión].
        viewModel.flag.observe(viewLifecycleOwner, Observer {
            if(it != null){
                Navigation.findNavController(requireView()).navigate(AdminFragmentDirections.actionAdminFragmentToLoginFragment())
            }
        })

        // Botón para ir a añadir aula
        binding.addRoomButton.setOnClickListener {
            Navigation.findNavController(it).navigate(AdminFragmentDirections.actionAdminFragmentToAddRoomFragment(viewModel.email.value.toString(),user))
        }
        // Botón para ir a editar aula
        binding.editRoomButton.setOnClickListener {
            Navigation.findNavController(it).navigate(AdminFragmentDirections.actionAdminFragmentToEditRoomFragment(viewModel.email.value.toString(),user))
        }
        // Botón para editar aula
        binding.deleteRoomButton.setOnClickListener {
            Navigation.findNavController(it).navigate(AdminFragmentDirections.actionAdminFragmentToDeleteRoomFragment(viewModel.email.value.toString(),user))
        }
        // Botón para ir cerrar sesión
        binding.imageButton.setOnClickListener {
            confirmDialogAlert(it.context,viewModel)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    // Este método muestra un dialogo de aviso.
    private fun confirmDialogAlert(it: Context, viewModel: AdminViewModel) {
        val builder = AlertDialog.Builder(it)
        val view = LayoutInflater.from(it).inflate(R.layout.custom_layout_dialog2,null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        dialog.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            viewModel.setFlag()
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
    }



}