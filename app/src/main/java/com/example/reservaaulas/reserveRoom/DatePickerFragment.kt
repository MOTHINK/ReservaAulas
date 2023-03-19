package com.example.reservaaulas.reserveRoom

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.reservaaulas.R
import java.util.*

class DatePickerFragment(val listener: (day:Int,month:Int,year:Int) -> Unit,val viewModel: ReserveRoomViewModel): DialogFragment(), DatePickerDialog.OnDateSetListener {
    // Sobre escribimos el método onDateSet para poder obtener el dia del mes, el mes y el año
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener(dayOfMonth, month,year)
    }

    // Este método sobre escrito se ejecuta al abrir el date picker dialog, obtiene el dia, el mes y el año
    // Además hemos hecho que solo aparezcan disponibles las 15 fechas siguientes a dia de hoy.
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_WEEK)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)

        val maxDayToReserve = 14 // aqui va el cambio para el campo dinamico de reserva permanente
        val milliOfDay = 86400000

        val picker = DatePickerDialog(activity as Context,this, year,month,day)
        picker.datePicker.minDate = c.timeInMillis
        picker.datePicker.maxDate = c.timeInMillis + (milliOfDay * maxDayToReserve).toLong()
        return picker
    }

}