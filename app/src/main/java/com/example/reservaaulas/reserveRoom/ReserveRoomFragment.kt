package com.example.reservaaulas.reserveRoom

import android.R.attr
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.reservaaulas.R
import com.example.reservaaulas.databinding.FragmentReserveRoomBinding
import kotlinx.coroutines.launch
import android.R.attr.button
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.widget.Button
import android.widget.TextView
import com.example.reservaaulas.deleteRoom.DeleteRoomViewModel
import com.example.reservaaulas.entities.PermanentReserve
import com.example.reservaaulas.entities.Reserve
import java.time.LocalDateTime
import java.time.temporal.ChronoField


class ReserveRoomFragment : Fragment() {

    // Creamos tres variables donde almacenaremos tanto los binding, como el viewmodel y su factory

    private lateinit var binding: FragmentReserveRoomBinding
    private lateinit var viewModel: ReserveRoomViewModel
    private lateinit var viewModelFactory: ReserveRoomViewModelFactory

    // Creamos una variable para guardar array de botones.
    private lateinit var reserveButtons: ArrayList<Button>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Instanciamos el binding para poder acceder a los elementos del diseño gráfico
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_reserve_room, container, false)

        // Instanciamos el viewmodel y su factory
        val application = requireNotNull(this.activity).application
        viewModelFactory = ReserveRoomViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(ReserveRoomViewModel::class.java)

        // Enlazamos el binding al viewmodel
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Este método selecciona la fecha de hoy
        onFirstDate()
        // Insertamos los botones un array list.
        insertReserveButtonsIntoArrayList()

        // Obtenemos los argumentos que se pasan por el action
        val args = ReserveRoomFragmentArgs.fromBundle(requireArguments())
        val door = args.doorRoom
        val email = args.userEmail
        val user = args.userName

        // Este método guarda la puerta del aula en el viewModel
        viewModel.setDoor(door)
        // Este método guarda el email en el viewModel.
        viewModel.setUserEmail(email)


        // Esta corrutina obtiene el aula por el nombre de la puerta.
        lifecycleScope.launch {
            viewModel.getRoomByDoor()
        }

        // Este observador observa si la lista room ha sido actualizada, si es así pues nos pone la imagen del aula
        // Muestra la modalidad, la puerta y el numero de asientos en la vista.
        viewModel.room.observe(viewLifecycleOwner, Observer {
            setRoomLogoImage(it[0].modality)
            binding.tvModality.text = it[0].modality
            binding.tvDoor.text = it[0].door
            binding.tvNseats.text = it[0].nSeats.toString()
        })

        // se llama a esta funcion que deshabilita todos los botones
        disableButtons()
        viewModel.flag.observe(viewLifecycleOwner,Observer{
            if(it != null){
                lifecycleScope.launch {
                    filterNonAvailableHours(viewModel.getReservesAndFilterAvailable(),
                        viewModel.getPermanentReservesByDoor(viewModel.door.value.toString()) as ArrayList<PermanentReserve>
                    )
                }
            }
        })


        // Este botón nos muestra el calendario para seleccionar una fecha.
        binding.etDatePicker.setOnClickListener {
            showDatePickerDialog()
        }

        // Este botón es para volver a la vista anterior.
        binding.backButton.setOnClickListener {
            Navigation.findNavController(it).navigate(ReserveRoomFragmentDirections.actionReserveRoomFragmentToUserFragment(email,user))
        }

        // Se llama a esta función para seleccionar una hora.
        selectAnHour()
        // Inflate the layout for this fragment
        return binding.root
    }

    // Este método crea la instancia de un datepicker para obtener un calendario
    private fun showDatePickerDialog(){
        val datePicker = DatePickerFragment({day,month,year -> onDateSelected(day, month, year)},viewModel)
        datePicker.show(parentFragmentManager,"datePicker")
    }

    // Este método selecciona los datos y los guarda en un viewModel además de lanzar una bandera una vez
    // Se selecciona una fecha.
    private fun onDateSelected(day:Int, month:Int, year:Int){
        binding.etDatePicker.setText("The selected date is: ${day}/${month+1}/${year}")
        viewModel.setDay(day)
        viewModel.setMonth(month+1)
        viewModel.setYear(year)

        viewModel.setFlag()
    }

    // Este método selecciona la fecha actual y la guarda en el viewModelm además lanza una bandera
    // para que luego nos lo actualice en la vista.
    private fun onFirstDate(){
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val currHour = current.get(ChronoField.HOUR_OF_DAY)
        val currDay = current.get(ChronoField.DAY_OF_MONTH)
        val currMonth = current.get(ChronoField.MONTH_OF_YEAR)
        val currYear = current.get(ChronoField.YEAR)

        viewModel.setDay(currDay)
        viewModel.setMonth(currMonth)
        viewModel.setYear(currYear)
        binding.etDatePicker.setText("The selected date is: ${currDay}/${currMonth}/${currYear}")

        viewModel.setFlag()
    }

    // Este método comprueba la modalidad y le da su respectivo logo.
    private fun setRoomLogoImage(modality: String){
        if(modality == "Science"){
            binding.logoRoom.setBackgroundResource(R.drawable.science)
        }
        if(modality == "Music"){
            binding.logoRoom.setBackgroundResource(R.drawable.music)
        }
        if(modality == "Informatics"){
            binding.logoRoom.setBackgroundResource(R.drawable.computer)
        }
        if(modality == "Art"){
            binding.logoRoom.setBackgroundResource(R.drawable.paint)
        }
        if(modality == "Meeting"){
            binding.logoRoom.setBackgroundResource(R.drawable.conversation)
        }
    }

    // Este método tiene dentro los botónes que determinan las horas que podemos seleccionar.
    private fun selectAnHour(){
        binding.am7.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,7)
        }
        binding.am8.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,8)
        }
        binding.am9.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,9)
        }
        binding.am10.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,10)
        }
        binding.am11.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,11)
        }
        binding.pm12.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,12)
        }
        binding.pm13.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,13)
        }
        binding.pm14.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,14)
        }
        binding.pm15.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,15)
        }
        binding.pm16.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,16)
        }
        binding.pm17.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,17)
        }
        binding.pm18.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,18)
        }
        binding.pm19.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,19)
        }
        binding.pm20.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,20)
        }
        binding.pm21.setOnClickListener {
            confirmDialogAlert(it.context,viewModel,21)
        }
    }

    // Este meétodo guarda todos los botones en un arra.
    private fun insertReserveButtonsIntoArrayList(){
        reserveButtons = arrayListOf(binding.am7,binding.am8,binding.am9,binding.am10,binding.am11,binding.pm12,
                                     binding.pm13,binding.pm14,binding.pm15,binding.pm16,binding.pm17,binding.pm18,
                                     binding.pm19,binding.pm20,binding.pm21)
    }

    // Este método deshabilita todos los botones
    private fun disableButtons(){
        for(button in reserveButtons){
            button.setBackgroundColor(resources.getColor(R.color.grey))
            button.isEnabled = false
        }
    }

    // Este método filtra las horas disponible, las horas no disponibles, las horas ocupadas y las horas reservadas permanentemente.
    private fun filterNonAvailableHours(reserveList: ArrayList<Reserve>,prhList: ArrayList<PermanentReserve>){

        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val currHour = current.get(ChronoField.HOUR_OF_DAY)
        val currDay = current.get(ChronoField.DAY_OF_MONTH)
        val currWeekDay = current.get(ChronoField.DAY_OF_WEEK)
        val currMonth = current.get(ChronoField.MONTH_OF_YEAR)
        val currYear = current.get(ChronoField.YEAR)

        for(button in reserveButtons){
            button.setBackgroundColor(resources.getColor(R.color.green))
            button.isEnabled = true
        }

        for(reserve in reserveList){
            for(i in 7..21){
                if(reserve.hour == i){
                    reserveButtons[i-7].setBackgroundColor(resources.getColor(R.color.red))
                    reserveButtons[i-7].isEnabled = false
                }
            }
        }

        for(prh in prhList){
            for(i in 7..21){
                if(prh.hour == i && prh.day == getWeekDayFromSelectedDate()){
                    reserveButtons[i-7].setBackgroundColor(resources.getColor(R.color.red))
                    reserveButtons[i-7].isEnabled = false
                }
            }
        }

        for(i in 7..21){
            if(currHour > i && currDay >= viewModel.day.value!!.toInt() && currMonth >= viewModel.month.value!!.toInt() && viewModel.year.value!!.toInt() >= currYear){
                reserveButtons[i-7].setBackgroundColor(resources.getColor(R.color.blue))
                reserveButtons[i-7].isEnabled = false
            }
        }

    }

    // Este método devuelve el dia de la semana según la fecha dd//mm//yyyy
    private fun getWeekDayFromSelectedDate(): String{
        var weekdayString = ""

        val weedayMap: Map<Int,String> = mapOf(0 to "Sunday", 1 to "Monday", 2 to "Tuesday", 3 to "Wednesday", 4 to "Thursday", 5 to "Friday", 6 to "Saturday")
        val monthsMap: Map<Int,Int> = mapOf(1 to 0,2 to 3,3 to 3,4 to 6, 5 to 1,6 to 4,7 to 6,8 to 2,9 to 5,10 to 0,11 to 3,12 to 5)
        val codYearFor20Century = 6

        var op1 = viewModel.day.value
        var op2 = monthsMap[viewModel.month.value]
        var op3 = codYearFor20Century
        var op4 = viewModel.year.value.toString().takeLast(2).toInt()
        var op5 = op4/4

        var sum = op1!! + op2!! + op3 + op4 + op5
        var getReminder = sum % 7

        weekdayString = weedayMap[getReminder].toString()
        return weekdayString
    }

    // Este método lanza un confirmDialog.
    private fun confirmDialogAlert(it: Context, viewModel: ReserveRoomViewModel,hour: Int) {
        val builder = AlertDialog.Builder(it)
        val view = LayoutInflater.from(it).inflate(R.layout.custom_layout_dialog3,null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        if(hour > 11) {
            dialog.findViewById<TextView>(R.id.textView2).setText("sure you want to book at: " + hour + " PM?")
        } else {
            dialog.findViewById<TextView>(R.id.textView2).setText("sure you want to book at: " + hour + " AM?")
        }

        dialog.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            viewModel.reserveAnHour(hour)
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
    }


}