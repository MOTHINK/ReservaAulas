package com.example.reservaaulas.addRoom

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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservaaulas.R
import com.example.reservaaulas.database.Database
import com.example.reservaaulas.databinding.FragmentAddRoomBinding
import com.example.reservaaulas.entities.PermanentReserve
import com.example.reservaaulas.entities.Room
import com.example.reservaaulas.login.LoginFragmentDirections
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class AddRoomFragment : Fragment() {

    // Creamos tres variables donde almacenaremos tanto los binding, como el viewmodel y su factory

    private lateinit var viewModel: AddRoomViewModel
    private lateinit var viewModelFactory: AddRoomViewModelFactory
    private lateinit var binding: FragmentAddRoomBinding

    // Creamos las variables correspondientes para guardar los spinner y los array que les corresponden.

    private lateinit var modalitySpinner: Spinner
    private lateinit var modalityList: Array<String>

    private lateinit var nSeatsSpinner: Spinner
    private lateinit var nSeatsLists: Array<Int>


    private lateinit var weekDaySpinner: Spinner
    private lateinit var weekDayList: MutableList<String>

    private lateinit var hourSpinner: Spinner
    private lateinit var hourList: MutableList<String>

    // Creamos una variable donde guardaremos el recyclerview.
    private lateinit var permanentReserveHourRecyclerView: RecyclerView
//    private lateinit var permanentReserveHourList: ArrayList<PermanentReserve>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Instanciamos el binding para poder acceder a los elementos del diseño gráfico
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_room, container, false)

        // Obtenemos los argumentos que se pasan por el action
        val args = AddRoomFragmentArgs.fromBundle(requireArguments())
        val email = args.adminEmail
        val user = args.userName

        // Añadir los items al spinner de modalidad
        addItemsToModalitySpinner()
        // seleccionar los items del spinner de modalidad
        selectedItemFromModalitySpinner()

        // Añadir los items al spinner de numero asientos
        addItemsToNumberOfSeatsSpinner()
        // seleccionar los items del spinner de numero asientos
        selectedItemFromNumberOfSeatsSpinner()

        // Añadir los itemos al spinner de dias de la semana
        addItemToWeekDaySpinner()
        // seleccionar los items del spinner de dias de la semana
        selectedItemFromWeekdaySpinner()

        // Añadir los itemos al spinner de la hora
        addItemToHourSpinner()
        // seleccionar los items del spinner de la hora.
        selectedItemFromHourSpinner()

        // Instanciamos el viewmodel y su factory
        val application = requireNotNull(this.activity).application
        viewModelFactory = AddRoomViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(AddRoomViewModel::class.java)

        // Enlazamos el binding al viewmodel
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // En este scope obtenemos las puertas disponibles.
        lifecycleScope.launch {
            viewModel.getAvailableDoors()
        }

        // Este botón llama a la función que lanza el flag
        binding.addPermanentReserve.setOnClickListener {
            viewModel.setFlag()
        }

        // Este botón sirve para borrar de la lista la reserva permanente y volver a refrescar el recyclerview si existe,
        // sino pues nos avisa de que no tenemos ninguna reserva permanente insertada.
        binding.removePermanentReserve.setOnClickListener {
            if(viewModel.removeLastPermanentReserveHourFromList()){
                addToRecyclerView()
                restartPermanentReserveSpinners()
                viewModel.setHourNull()
                viewModel.setWeekdayNull()
            } else {
                showSnackBar(binding.imageView3,"There is no reserve yet", ResourcesCompat.getColor(resources, R.color.red, null))
            }

        }

        // Este observador se ejecuta cuando el flag es lanzada lo que hace es:
        //  1. Comprobar que los campos no esté vacios, si es así nos muestra un error, sino sigue con la secuencia de ejcución de código
        //  2. Se insertan los datos en su objeto pertinente
        //  3. Comprueba que esa reserva permanente ya no existe
        //  4. Se reinicia el spinner y se vuelve a poner los campos en null,
        viewModel.flag.observe(viewLifecycleOwner, Observer {
            if(it != null){
                if(viewModel.hour.value == null || viewModel.weekday.value == null){
                    showSnackBar(binding.imageView3,"Please, fill hour and weekday", ResourcesCompat.getColor(resources, R.color.red, null))
                } else {
                    val correctHourFormat = viewModel.hour.value!!.split(":")
                    var r = PermanentReserve("",viewModel.weekday.value.toString(),correctHourFormat[0].toInt())
                    if(!checkIfThePermanentReserveExists(r)){
                        viewModel.addPermanentReserveHourToList(r)
                        addToRecyclerView()
                        restartPermanentReserveSpinners()
                        viewModel.setHourNull()
                        viewModel.setWeekdayNull()
                    } else {
                        showSnackBar(binding.imageView3,"This reserve already exists", ResourcesCompat.getColor(resources, R.color.red, null))
                    }
                }
            }
        })

        // Este botón añade el aula a la base de datos y también las reservas permanentes, para ello sigue los siguientes pasos:
        //  1. Comprueba que todos los campos están rellenados.
        //  2. Comprueba que el aula no existe previamente
        //  3. Guarda el aula en la base de datos y refresca el recyclerview.
        binding.addButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.setDoor(binding.doorEditText.text.toString())
                if(viewModel.checkSelectedOptions()){
                    if(!setThePrintedDoor(binding.doorEditText.text.toString())){
                        viewModel.saveRoomInDatabase()
                        viewModel.savePermanentReserveHourInDB()
                        viewModel.permanentReserveHourList.value!!.clear()
                        addToRecyclerView()
                        restartSpinner()
                        binding.doorEditText.text.clear()
                        showSnackBar(it,"Room is correctly Added", ResourcesCompat.getColor(resources, R.color.green, null))
                    } else {
                        restartSpinner()
                        showSnackBar(it,"This class is already in use", ResourcesCompat.getColor(resources, R.color.red, null))
                    }
                } else {
                    showSnackBar(it,"Options are not selected", ResourcesCompat.getColor(resources, R.color.red, null))
                }
            }
        }


        // Este butón se para volver al fragmento anterior.
        binding.backButton.setOnClickListener {
            Navigation.findNavController(it).navigate(AddRoomFragmentDirections.actionAddRoomFragmentToAdminFragment(email,user))
        }

        // Inflate the layout for this fragment
        return binding.root
    }


    // Este método compruba que en la lista que guardamos las reservas permanentes no exita reservas repetidas.
    private fun checkIfThePermanentReserveExists(prh: PermanentReserve): Boolean {
        var exists = false
        for(p in viewModel.permanentReserveHourList.value!!){
            if(p.day == prh.day && p.hour == prh.hour){
                exists = true
            }
        }
        return exists
    }

    // Este método sirve para añadir los datos al recyclerview tanto al inicio que como cuando se realiza modificaciones
    private fun addToRecyclerView(){
        permanentReserveHourRecyclerView = binding.permanentHourRecycleview
        permanentReserveHourRecyclerView.layoutManager = LinearLayoutManager(activity)
        permanentReserveHourRecyclerView.setHasFixedSize(true)
        permanentReserveHourRecyclerView.adapter = PermanentReserveAdapter(viewModel.permanentReserveHourList.value as ArrayList<PermanentReserve>,viewModel)
    }

    // Este método llama a la base de datos y comprueba que la puerta esté disponible.
    private suspend fun setThePrintedDoor(door: String): Boolean {
        return viewModel.checkDoorExistsInDB(door)
    }

    //*********************** Estos metodos son para iniciar los spinner ***********************/

    private fun addItemToWeekDaySpinner(){
        weekDaySpinner = binding.spinner4
        weekDaySpinner.setPrompt("weekday");
        weekDayList = resources.getStringArray(R.array.weekdays).toMutableList()
        val adapter = ArrayAdapter<String>(requireActivity(),android.R.layout.simple_spinner_item,weekDayList)
        weekDaySpinner.adapter = adapter
    }


    private fun selectedItemFromWeekdaySpinner() {
        weekDaySpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(weekDayList[position] != "None"){
                    viewModel.setWeekday(weekDayList[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun addItemToHourSpinner(){
        hourSpinner = binding.spinner5
        hourSpinner.setPrompt("hour");
        hourList = resources.getStringArray(R.array.hours).toMutableList()
        val adapter = ArrayAdapter<String>(requireActivity(),android.R.layout.simple_spinner_item,hourList)
        hourSpinner.adapter = adapter
    }

    private fun selectedItemFromHourSpinner() {
        hourSpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(hourList[position] != "None"){
                    viewModel.setHour(hourList[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }


    private fun addItemsToModalitySpinner(){
        modalitySpinner = binding.spinner
        modalitySpinner.setPrompt("Modality");
        modalityList = resources.getStringArray(R.array.modality)
        val adapter = ArrayAdapter<String>(requireActivity(),android.R.layout.simple_spinner_item,modalityList)
        modalitySpinner.adapter = adapter
    }

    private fun selectedItemFromModalitySpinner() {
        modalitySpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(modalityList[position] != "None"){
                    viewModel.setModality(modalityList[position])
                } else {
                    viewModel.setModalityNull()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }


    private fun addItemsToNumberOfSeatsSpinner(){
        nSeatsSpinner = binding.spinner3
        nSeatsSpinner.setPrompt("nSeats");
        nSeatsLists = resources.getIntArray(R.array.nSeats).toTypedArray()
        val adapter = ArrayAdapter<Int>(requireActivity(),android.R.layout.simple_spinner_item,nSeatsLists)
        nSeatsSpinner.adapter = adapter
    }

    private fun selectedItemFromNumberOfSeatsSpinner() {
        nSeatsSpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(nSeatsLists[position] != 0){
                    viewModel.setNumberOfSeats(nSeatsLists[position])
                } else {
                    viewModel.setNumberOfSeatsNull()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
    //**************************** Fin *******************************


    // Este método nos muestra un snackbar
    private fun showSnackBar(view: View,message: String,color: Int) {
        val err = message
        Snackbar.make(view,err, Snackbar.LENGTH_LONG)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
            .setDuration(2000)
            .setBackgroundTint(color) // Color.parseColor("#006400")
            .show()
    }

    // Este método sirve para resetear los spinner
    private fun restartSpinner(){
        modalitySpinner.setSelection(0)
        nSeatsSpinner.setSelection(0)
    }

    // Este método sirve para resetear los spinners pertinentes de reserva permanente
    private fun restartPermanentReserveSpinners(){
        weekDaySpinner.setSelection(0)
        hourSpinner.setSelection(0)
    }
}