package com.example.reservaaulas.searchRooms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservaaulas.R
import com.example.reservaaulas.databinding.FragmentSearchRoomsBinding
import com.example.reservaaulas.entities.Room
import com.example.reservaaulas.myReserves.MyReservesAdapter
import com.example.reservaaulas.user.UserViewModel
import com.example.reservaaulas.user.UserViewModelFactory
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class SearchRoomsFragment(private val userEmail: String,private val userName: String) : Fragment() {

    // Creamos tres variable donde guardaremos más tarde el binding de este fragmento, el viewmodel y su factory

    private lateinit var binding: FragmentSearchRoomsBinding
    private lateinit var viewModel: SearchRoomsViewModel
    private lateinit var viewModelFactory: SearchRoomsViewModelFactory

    // Creamos tres variables donde irán los spinners, uno para la modalidad otro para las puertas y otro para el numero de asientos con sus respectivas
    // listas

    private lateinit var modalitySpinner: Spinner
    private lateinit var modalityList: Array<String>

    private lateinit var doorSpinner: Spinner
    private lateinit var doorList: List<String>

    private lateinit var nSeatsSpinner: Spinner
    private lateinit var nSeatsLists: Array<Int>

    // Creamos una variable donde guardaremos el recyclerview y su correspondiente lista.

    private lateinit var roomRecyclerView: RecyclerView
    private lateinit var roomArrayList: ArrayList<Room>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Instanciamos el binding para poder acceder a los elementos del diseño gráfico
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_search_rooms, container, false)

        // Guardamos los parametros que recibimos en unas variables
        var email = userEmail
        var user = userName


        // Añadimos los items de modalidad al spinner
        addItemsToModalitySpinner()
        // Seleccionamos los items del spinner de modalidad
        selectedItemFromModalitySpinner()

        // Añadimos los items de numero de asientos al spinner
        addItemsToNumberOfSeatsSpinner()
        // Seleccionamos los items del spinner de numero de asientos
        selectedItemFromNumberOfSeatsSpinner()

        // Instanciamos el viewmodel y su factory
        val application = requireNotNull(this.activity).application
        viewModelFactory = SearchRoomsViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(SearchRoomsViewModel::class.java)

        // Enlazamos el binding al viewmodel
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        // Guardamos el nombre del usuario y su email en el viewModel
        viewModel.setUserEmail(email)
        viewModel.setUserName(user)


        // En esta corrutina obtenemos desde la base de datos las puertas que están en uso.
        lifecycleScope.launch {
            viewModel.getUsedDoors()
        }

        // Este observador observa si la lista usedDoor ha sido modificada, y cada que se modifique actualiza el spinner de puerta
        viewModel.usedDoors.observe(viewLifecycleOwner, Observer {
            if(it != null){
                addItemsToDoorSpinner(it)
                selectedItemFromDoorSpinner()
            }
        })

        // Este observador observa si se lanza una bandera, la cual comprueba si hemos seleccionado algo del spinner de puerta,
        // en ese caso, deshabilita los otros dos spinners.
        viewModel.searchOnlyByDoorFlag.observe(viewLifecycleOwner, Observer {
            if(it == true){
                binding.modalitySpinner.isEnabled = false
                binding.nSeatsSpinner.isEnabled = false
            } else {
                binding.modalitySpinner.isEnabled = true
                binding.nSeatsSpinner.isEnabled = true
            }
        })

        // Este observador lanza una bandera la cual sirve para que llame una función que trae desde la base de datos las aulas filtradas.
        viewModel.filteringFlag.observe(viewLifecycleOwner, Observer {
            if(it != null){
                viewModel.filterRooms()
            }
        })

        // Instanciamos el recyclerView
        roomRecyclerView = binding.searchRoomRecycleview
        roomRecyclerView.layoutManager = LinearLayoutManager(activity)
        roomRecyclerView.setHasFixedSize(true)
        // Instanciamos la lista correspondiente al recyclerview
        roomArrayList = arrayListOf<Room>()

        // Esta corrutina llama a una función que trae desde la base de datos todas las aulas disponible.
        lifecycleScope.launch {
            viewModel.getAllRooms()
        }

        // Este observador observa si ha habido modificación en la lista de aulas, si es así pues refresca
        // el recyclerview y muestra las aulas.
        viewModel.rooms.observe(viewLifecycleOwner, Observer {
            if(it != null){
                roomRecyclerView.adapter = SearchRoomsAdapter(it,viewModel)
            }
        })

        // Este botón lanza la bandera para filtrar las aulas.
        binding.filterButton.setOnClickListener {
            viewModel.filteringFlag()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    // Este método llama a un snackbar, para mostrar un mensaje
    private fun showSnackBar(view: View,message: String,color: Int) {
        val err = message
        Snackbar.make(view,err, Snackbar.LENGTH_LONG)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
            .setDuration(2000)
            .setBackgroundTint(color) // Color.parseColor("#006400")
            .show()
    }


    //****************** Los siguientes métodos sirven para rellenar los spinners con sus respectivos items *********/
    private fun addItemsToModalitySpinner(){
        modalitySpinner = binding.modalitySpinner
        modalitySpinner.setPrompt("Modality");
        modalityList = resources.getStringArray(R.array.modality)
        modalityList.set(0,"All")
        val adapter = ArrayAdapter<String>(requireActivity(),android.R.layout.simple_spinner_item,modalityList)
        modalitySpinner.adapter = adapter
    }

    private fun selectedItemFromModalitySpinner() {
        modalitySpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setModality(modalityList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }


    private fun addItemsToDoorSpinner(availableDoors: List<String>){
        doorSpinner = binding.doorSpinner
        doorSpinner.setPrompt("Door");
        doorList = availableDoors
        val adapter = ArrayAdapter<String>(requireActivity(),android.R.layout.simple_spinner_item,doorList)
        doorSpinner.adapter = adapter
    }

    private fun selectedItemFromDoorSpinner() {
        doorSpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setDoor(doorList[position])
                viewModel.searchOnlyByDoorFlag()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun addItemsToNumberOfSeatsSpinner(){
        nSeatsSpinner = binding.nSeatsSpinner
        nSeatsSpinner.setPrompt("Modality");
        nSeatsLists = resources.getIntArray(R.array.nSeats).toTypedArray()
        val adapter = ArrayAdapter<Int>(requireActivity(),android.R.layout.simple_spinner_item,nSeatsLists)
        nSeatsSpinner.adapter = adapter
    }

    private fun selectedItemFromNumberOfSeatsSpinner() {
        nSeatsSpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setNumberOfSeats(nSeatsLists[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
    //********************************************************************/
}