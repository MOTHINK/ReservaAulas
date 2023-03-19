package com.example.reservaaulas.myReserves

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.reservaaulas.R
import com.example.reservaaulas.databinding.ReservedItemBinding
import com.example.reservaaulas.deleteRoom.DeleteRoomAdapter
import com.example.reservaaulas.deleteRoom.DeleteRoomViewModel
import com.example.reservaaulas.entities.Room
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.time.temporal.ChronoField

class MyReservesAdapter(private val roomList: ArrayList<Room>, private val viewModel: MyReservesViewModel)
    :RecyclerView.Adapter<MyReservesAdapter.MyViewHolder>()  {
    private lateinit var id: String
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val binding = ReservedItemBinding.bind(itemView)

        val modality = binding.TVModality
        val door = binding.tvDoor
        val nSeats = binding.tvNumSeats
        val image = binding.imageIcon
        val cv = binding.cv

        val reserveDate = binding.tvReserveDate
        val reserveHour = binding.tvReserveHour

        fun render(viewModel: MyReservesViewModel,reserveId: String){

            if(modality.text.toString() == "Science"){
                image.setBackgroundResource(R.drawable.science)
            }

            if(modality.text.toString() == "Music"){
                image.setBackgroundResource(R.drawable.music)
            }

            if(modality.text.toString() == "Informatics"){
                image.setBackgroundResource(R.drawable.computer)
            }

            if(modality.text.toString() == "Art"){
                image.setBackgroundResource(R.drawable.paint)
            }

            if(modality.text.toString() == "Meeting"){
                image.setBackgroundResource(R.drawable.conversation)
            }


            binding.cv.setOnClickListener {
                confirmDialogAlert(it.context, viewModel, reserveId)
            }
            binding.deleteButton.setOnClickListener {
                confirmDialogAlert(it.context, viewModel, reserveId)
            }
        }

        private fun showSnackBar(view: View,message: String,color: Int) {
            val err = message
            Snackbar.make(view,err, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .setDuration(2000)
                .setBackgroundTint(color) // Color.parseColor("#006400")
                .show()
        }

        private fun confirmDialogAlert(it: Context, viewModel: MyReservesViewModel, reserveId: String) {
            val builder = AlertDialog.Builder(it)
            val view = LayoutInflater.from(it).inflate(R.layout.custom_layout_dialog,null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()

            dialog.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                // call delete reserve function from viewModel
                viewModel.deleteUserReserve(reserveId)
                viewModel.setFlag()
                dialog.dismiss()
            }

            dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.reserved_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyReservesAdapter.MyViewHolder, position: Int) {
        val currentRoom = roomList[position]
        val currReserve = viewModel.reserves.value!![position]

        id = currReserve.hour.toString() + currReserve.day.toString() + currReserve.month.toString() + currReserve.year.toString() + viewModel.userEmail.value.toString() + currentRoom.door.toString()
        holder.modality.text = currentRoom.modality
        holder.door.text = currentRoom.door
        holder.nSeats.text = currentRoom.nSeats.toString()
        holder.cv.id = position+1

        holder.reserveDate.text = currReserve.day.toString() + "/" + currReserve.month.toString() + "/" + currReserve.year.toString()

        if(currReserve.hour > 11){
            holder.reserveHour.text = currReserve.hour.toString() + " PM"
        } else {
            holder.reserveHour.text = currReserve.hour.toString() + " AM"
        }

        holder.render(viewModel,id)
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

}