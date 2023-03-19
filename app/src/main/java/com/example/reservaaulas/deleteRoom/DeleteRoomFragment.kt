package com.example.reservaaulas.deleteRoom

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
import com.example.reservaaulas.addRoom.AddRoomFragmentArgs
import com.example.reservaaulas.addRoom.AddRoomViewModel
import com.example.reservaaulas.addRoom.AddRoomViewModelFactory
import com.example.reservaaulas.admin.AdminFragmentDirections
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.databinding.FragmentDeleteRoomBinding
import com.example.reservaaulas.entities.Room
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class DeleteRoomFragment : Fragment() {

    // Creamos tres variables donde almacenaremos tanto los binding, como el viewmodel y su factory

    private lateinit var binding: FragmentDeleteRoomBinding
    private lateinit var viewModel: DeleteRoomViewModel
    private lateinit var viewModelFactory: DeleteRoomViewModelFactory

    // Creamos las variables para el recyclerview y su lista pertinente

    private lateinit var roomRecyclerView: RecyclerView
    private lateinit var roomArrayList: ArrayList<Room>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Instanciamos el binding para poder acceder a los elementos del diseño gráfico
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_delete_room, container, false)

        // Obtenemos los argumentos que se pasan por el action
        val args = DeleteRoomFragmentArgs.fromBundle(requireArguments())
        val email = args.adminEmail
        val user = args.userName

        // Instanciamos el viewmodel y su factory
        val application = requireNotNull(this.activity).application
        viewModelFactory = DeleteRoomViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(DeleteRoomViewModel::class.java)

        // Enlazamos el binding al viewmodel
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Guardamos el email en el viewModel
        viewModel.setUserEmail(email)

        // Instanciamos el recyclerview.
        roomRecyclerView = binding.deleteRoomRecycleview
        roomRecyclerView.layoutManager = LinearLayoutManager(activity)
        roomRecyclerView.setHasFixedSize(true)

        // Instanciamos la lista para el recyclerview
        roomArrayList = arrayListOf<Room>()

        // En este scope se obtiene desde la base todas las aulas disponibles.
        lifecycleScope.launch {
            viewModel.getAllRooms()
        }

        // Este observador. comprueba si ha habido cambios en la lista de aulas. para poder refrescar el recyclerviwe.
        viewModel.rooms.observe(viewLifecycleOwner, Observer {
            if(it != null){
                roomRecyclerView.adapter = DeleteRoomAdapter(it,viewModel)
            }
        })

        // Este observador, observa el flag, si ha habido un cambio, vuelve a llamar al método de obtener
        // Todas las aulas para luego insrtarlos en su lista, así nos permite refrescar el recyclerview.
        viewModel.flag.observe(viewLifecycleOwner, Observer {
            if (it != null){
                lifecycleScope.launch {
                    viewModel.getAllRooms()
                }
            }
        })

        // Este observador, comprueba que la bandera delete se ha modificado, cuando eso ocurre nos muestra un snackbar de que hemos borrado con éxito.
        viewModel.delete.observe(viewLifecycleOwner, Observer {
            if(it == true){
                showSnackBar(requireView(),"Deleting Room ${viewModel.deletedRoom.value.toString()}", ResourcesCompat.getColor(resources, R.color.red, null))
                viewModel.deleteFlag()
            }
        })


        // Este botón nos devuelve a la vista anterior.
        binding.btnBack.setOnClickListener {
            Navigation.findNavController(it).navigate(DeleteRoomFragmentDirections.actionDeleteRoomFragmentToAdminFragment(viewModel.email.value.toString(),user))
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    // Este método sirve para mostrar un snackbar.
    private fun showSnackBar(view: View,message: String,color: Int) {
        val err = message
        Snackbar.make(view,err, Snackbar.LENGTH_LONG)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
            .setDuration(2000)
            .setBackgroundTint(color) // Color.parseColor("#006400")
            .show()
    }

}