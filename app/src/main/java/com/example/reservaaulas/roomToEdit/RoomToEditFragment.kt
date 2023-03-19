package com.example.reservaaulas.roomToEdit

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.reservaaulas.R
import com.example.reservaaulas.databinding.FragmentRoomToEditBinding
import com.example.reservaaulas.deleteRoom.DeleteRoomViewModel
import com.example.reservaaulas.editRoom.EditRoomFragmentArgs
import com.example.reservaaulas.editRoom.EditRoomFragmentDirections
import com.example.reservaaulas.editRoom.EditRoomViewModel
import com.example.reservaaulas.editRoom.EditRoomViewModelFactory
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class RoomToEditFragment : Fragment() {

    // Creamos tres variables donde almacenaremos tanto los binding, como el viewmodel y su factory

    private lateinit var binding: FragmentRoomToEditBinding
    private lateinit var viewModel: RoomToEditViewModel
    private lateinit var viewModelFactory: RoomToEditViewModelFactory

    // Creamos las variables donde guardaremos los spinners y sus listas pertinentes.

    private lateinit var modalitySpinner: Spinner
    private lateinit var modalityList: Array<String>

    private lateinit var doorSpinner: Spinner
    private lateinit var doorList: MutableList<String>

    private lateinit var nSeatsSpinner: Spinner
    private lateinit var nSeatsLists: Array<Int>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Instanciamos el binding para poder acceder a los elementos del diseño gráfico
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_room_to_edit, container, false)

        // Obtenemos los argumentos que se pasan por el action
        val args = RoomToEditFragmentArgs.fromBundle(requireArguments())
        val email = args.adminEmail
        val roomToEdit = args.roomToEdit
        val user = args.userName

        // Instanciamos el viewmodel y su factory
        val application = requireNotNull(this.activity).application
        viewModelFactory = RoomToEditViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(RoomToEditViewModel::class.java)

        // Enlazamos el binding al viewmodel
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // le damos al textview con valor al inicio de la aplicación.
        binding.textView.text = "Edit Room: " + roomToEdit

        // Guardamos el email en el viewModel.
        viewModel.setUserEmail(email)

        // En este corrutina se obtiene:
        // 1. el aula el cual se quiere editar.
        // 2. se pasa los datos a los spinner de esta aula.
        lifecycleScope.launch {
            viewModel.getRoomToEdit(roomToEdit)
            // Add items to modality spinner
            addItemsToModalitySpinner()
            // Select item from modality spinner
            selectedItemFromModalitySpinner(viewModel.modality.value.toString())

            // Add item to number of seats spinner
            addItemsToNumberOfSeatsSpinner()
            // Select item from number of seats spinner
            selectedItemFromNumberOfSeatsSpinner(viewModel.nSeats.value!!.toInt())
        }


        // Este corrutinas obtiene las puertas disponibles
        lifecycleScope.launch {
            viewModel.getAvailableDoors()
        }


        // Este observador comprueba si la lista ha sido modificada, si es así pues actualiza el spinner.
        viewModel.availableDoors.observe(viewLifecycleOwner, Observer {
            if(it != null){
                addItemsToDoorSpinner(it,roomToEdit)
                selectedItemFromDoorSpinner(viewModel.door.value.toString())
            }
        })

        // Este observador comprueba que si se ha lanzado esta bandera, si es así, pues se modifica el aula.
        // Una vez eso, nos devuelve a la vista anterior, y nos muestra un snackbar con el mensaje de éxito.
        viewModel.confirmEditFlag.observe(viewLifecycleOwner, Observer {
            if(it == true){
                viewModel.updateRoom(roomToEdit)
                Navigation.findNavController(requireView()).navigate(RoomToEditFragmentDirections.actionRoomToEditFragmentToEditRoomFragment(viewModel.email.value.toString(),user))
                showSnackBar(requireView(),"Room updated Successfully", ResourcesCompat.getColor(resources, R.color.green, null))
                viewModel.confirmEditFlag()
            }
        })

        // Este botón muestra un dialog el cual nos avisa si queremos seguir, si es así pues lanza la bandera para realizar la modificación del aula.
        binding.EditButton.setOnClickListener {
            confirmDialogAlert(it.context, viewModel)
        }


        // Este botón nos devuelve a la vista anterior.
        binding.backButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(RoomToEditFragmentDirections.actionRoomToEditFragmentToEditRoomFragment(viewModel.email.value.toString(),user))
        }

        // Inflate the layout for this fragment
        return binding.root
    }


    // Esta función muestra un snackbar.
    private fun showSnackBar(view: View,message: String,color: Int) {
        val err = message
        Snackbar.make(view,err, Snackbar.LENGTH_LONG)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
            .setDuration(2000)
            .setBackgroundTint(color) // Color.parseColor("#006400")
            .show()
    }

    //******************** Estos métodos siguientes son para insertar los datos en los spinners ******/
    private fun addItemsToModalitySpinner(){
        modalitySpinner = binding.spinner
        modalitySpinner.setPrompt("Modality");
        modalityList = resources.getStringArray(R.array.modality)
        val adapter = ArrayAdapter<String>(requireActivity(),android.R.layout.simple_spinner_item,modalityList)
        modalitySpinner.adapter = adapter
    }

    private fun selectedItemFromModalitySpinner(modalityOption: String) {
        modalitySpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(modalityList[position] != "None"){
                    viewModel.setModality(modalityList[position])
                } else {
                    modalitySpinner.setSelection(setDefaultModality(modalityList,modalityOption))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun setDefaultModality(modalityList: Array<String>,modality: String): Int {
        var n = -1
        for (i in 0..5) {
            if(modalityList[i] == modality){
                n = i
            }
        }
        return n
    }


    private fun addItemsToDoorSpinner(availableDoors: List<String>,doorOption: String){
        doorSpinner = binding.spinner2
        doorSpinner.setPrompt("Door");
        doorList = availableDoors as MutableList<String>
        doorList.add(1,doorOption)
        val adapter = ArrayAdapter<String>(requireActivity(),android.R.layout.simple_spinner_item,doorList)
        doorSpinner.adapter = adapter
    }

    private fun selectedItemFromDoorSpinner(doorOption: String) {
        doorSpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(doorList[position] != "None"){
                    viewModel.setDoor(doorList[position])
                } else {
                    doorSpinner.setSelection(1)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun addItemsToNumberOfSeatsSpinner(){
        nSeatsSpinner = binding.spinner3
        nSeatsSpinner.setPrompt("Modality");
        nSeatsLists = resources.getIntArray(R.array.nSeats).toTypedArray()
        val adapter = ArrayAdapter<Int>(requireActivity(),android.R.layout.simple_spinner_item,nSeatsLists)
        nSeatsSpinner.adapter = adapter
    }

    private fun selectedItemFromNumberOfSeatsSpinner(nSeatsOption: Int) {
        nSeatsSpinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(nSeatsLists[position] != 0){
                    viewModel.setNumberOfSeats(nSeatsLists[position])
                } else {
                    nSeatsSpinner.setSelection(setDefaultNumSeats(nSeatsLists,nSeatsOption))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun setDefaultNumSeats(nSeatLists: Array<Int>,nSeatsOption: Int): Int {
        var n = -1
        for (i in 0..31) {
            if(nSeatLists[i] == nSeatsOption){
                n = i
            }
        }
        return n
    }
    //********************************************************/

    //Este método sirve para lanzar un dialog.
    private fun confirmDialogAlert(it: Context, viewModel: RoomToEditViewModel) {
        val builder = AlertDialog.Builder(it)
        val view = LayoutInflater.from(it).inflate(R.layout.custom_layout_dialog,null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        dialog.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            viewModel.confirmEditFlag()
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
    }

}