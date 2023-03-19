package com.example.reservaaulas.addRoom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.reservaaulas.R
import com.example.reservaaulas.databinding.PermanentHourItemBinding
import com.example.reservaaulas.entities.PermanentReserve
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class PermanentReserveAdapter(private val permanentReserveHourList: ArrayList<PermanentReserve>, private val viewModel: AddRoomViewModel):
    RecyclerView.Adapter<PermanentReserveAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding = PermanentHourItemBinding.bind(itemView)

        val weekday = binding.weekday
        val hour = binding.hour
        var currHour = -1
        var currId = -1


        fun render(viewModel: AddRoomViewModel){

        }

        private fun showSnackBar(view: View, message: String, color: Int) {
            val err = message
            Snackbar.make(view,err, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .setDuration(2000)
                .setBackgroundTint(color) // Color.parseColor("#006400")
                .show()
        }

    }

    // Este método sobreescrito crea el viewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.permanent_hour_item,parent,false)
        return PermanentReserveAdapter.MyViewHolder(itemView)
    }

    // Este método sobreescrito genera los items para el Recyclerview, pasando los datos necesarios para identificación.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currPermanentReserveHourList = permanentReserveHourList[position]

        holder.weekday.text = currPermanentReserveHourList.day
        if(currPermanentReserveHourList.hour > 11){
            holder.hour.text = currPermanentReserveHourList.hour.toString() + " PM"
        } else {
            holder.hour.text = currPermanentReserveHourList.hour.toString() + " AM"
        }

        holder.currId = position+7
        holder.currHour = currPermanentReserveHourList.hour

        holder.render(viewModel)
    }

    // Este método sobreescrito permite obtener el tamaño de la lista, para luego iterarla en el método onBindViewHolder().
    override fun getItemCount(): Int {
       return permanentReserveHourList.size
    }

}