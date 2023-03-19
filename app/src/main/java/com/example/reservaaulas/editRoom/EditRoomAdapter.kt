//package com.example.reservaaulas.deleteRoom

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.reservaaulas.R
import com.example.reservaaulas.databinding.EditRoomItemBinding
import com.example.reservaaulas.databinding.RoomItemBinding
import com.example.reservaaulas.editRoom.EditRoomViewModel

import com.example.reservaaulas.entities.Room
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class EditRoomAdapter(private val roomList: ArrayList<Room>,private val viewModel: EditRoomViewModel):
    RecyclerView.Adapter<EditRoomAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val binding = EditRoomItemBinding.bind(itemView)

        // En cada iteración, se obtienen los elementos de la vista, y se les pasa sus respectivos datos.

        val modality = binding.TVModality
        val door = binding.tvDoor
        val nSeats = binding.tvNumSeats
        val image = binding.imageIcon
        val cv = binding.cv


        fun render(viewModel: EditRoomViewModel){
            // Dentro de este render, comprobamos que modalidad tiene la clase, y según ello le pasamos
            // Su respectivo logo.
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


            // Estos dos botónes son para llamar a un alert dialog para avisar si queremos ir a editar o no.

            binding.cv.setOnClickListener {
                viewModel.setRoomToEdit(door.text.toString())
                viewModel.editCertainRoomFlag()
            }
            binding.editButton.setOnClickListener {
                viewModel.setRoomToEdit(door.text.toString())
                viewModel.editCertainRoomFlag()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.edit_room_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRoom = roomList[position]
        holder.modality.text = currentRoom.modality
        holder.door.text = currentRoom.door
        holder.nSeats.text = currentRoom.nSeats.toString()
        holder.cv.id = position+1

        holder.render(viewModel)
    }

    override fun getItemCount(): Int {
        return roomList.size
    }



}