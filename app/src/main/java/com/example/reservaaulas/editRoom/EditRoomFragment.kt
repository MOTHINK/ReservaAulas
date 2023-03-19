package com.example.reservaaulas.editRoom

import EditRoomAdapter
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservaaulas.R
import com.example.reservaaulas.admin.AdminFragmentDirections
import com.example.reservaaulas.databinding.FragmentEditRoomBinding
import com.example.reservaaulas.deleteRoom.*
import com.example.reservaaulas.entities.Room
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class EditRoomFragment : Fragment() {

    // Creamos tres variables donde almacenaremos tanto los binding, como el viewmodel y su factory

    private lateinit var binding: FragmentEditRoomBinding
    private lateinit var viewModel: EditRoomViewModel
    private lateinit var viewModelFactory: EditRoomViewModelFactory

    // Creamos las variables para el recyclerview y su lista pertinente

    private lateinit var roomRecyclerView: RecyclerView
    private lateinit var roomArrayList: ArrayList<Room>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Instanciamos el binding para poder acceder a los elementos del diseño gráfico
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_room, container, false)

        // Obtenemos los argumentos que se pasan por el action
        val args = EditRoomFragmentArgs.fromBundle(requireArguments())
        val email = args.adminEmail
        val user = args.userName

        // Instanciamos el viewmodel y su factory
        val application = requireNotNull(this.activity).application
        viewModelFactory = EditRoomViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(EditRoomViewModel::class.java)

        // Enlazamos el binding al viewmodel
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Guardamos el email del usuario en el viewModel
        viewModel.setUserEmail(email)

        // Instanciamos el recyclerview
        roomRecyclerView = binding.editRoomRecycleview
        roomRecyclerView.layoutManager = LinearLayoutManager(activity)
        roomRecyclerView.setHasFixedSize(true)
        // Instanciamos la lista pertinente al recyclerview.
        roomArrayList = arrayListOf<Room>()

        // Esta corrutina llama a una función para obtener todas las aulas disponibles.
        lifecycleScope.launch {
            viewModel.getAllRooms()
        }

        // Este observador comprueba si ha habido un cambio el la lista de aulas, si es así pues nos actualiza el recyclerview.
        viewModel.rooms.observe(viewLifecycleOwner, Observer {
            if(it != null){
                roomRecyclerView.adapter = EditRoomAdapter(it,viewModel)
            }
        })

        // Este observador comprueba si la bandera editCertainRoomFlag ha sido lanzada, si es así pues nos envía a la vista donde se podrá modificar.
        viewModel.editCertainRoomFlag.observe(viewLifecycleOwner, Observer {
            if(it != null){
                Navigation.findNavController(requireView()).navigate(EditRoomFragmentDirections.actionEditRoomFragmentToRoomToEditFragment(viewModel.email.value.toString(),viewModel.roomToEdit.value.toString(),user))
            }
        })

        // Este botón nos permite para volver a la vista anterior.
        binding.btnBack2.setOnClickListener {
            Navigation.findNavController(it).navigate(EditRoomFragmentDirections.actionEditRoomFragmentToAdminFragment(viewModel.email.value.toString(),user))
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    // Este método lanza un snackbar
    private fun showSnackBar(view: View,message: String,color: Int) {
        val err = message
        Snackbar.make(view,err, Snackbar.LENGTH_LONG)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
            .setDuration(2000)
            .setBackgroundTint(color) // Color.parseColor("#006400")
            .show()
    }

}