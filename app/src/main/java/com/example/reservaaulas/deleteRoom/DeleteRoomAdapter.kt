package com.example.reservaaulas.deleteRoom

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.reservaaulas.R
import com.example.reservaaulas.databinding.RoomItemBinding

import com.example.reservaaulas.entities.Room
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class DeleteRoomAdapter(private val roomList: ArrayList<Room>,private val viewModel: DeleteRoomViewModel):
    RecyclerView.Adapter<DeleteRoomAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val binding = RoomItemBinding.bind(itemView)

        // En cada iteración, se obtienen los elementos de la vista, y se les pasa sus respectivos datos.

        val modality = binding.TVModality
        val door = binding.tvDoor
        val nSeats = binding.tvNumSeats
        val image = binding.imageIcon
        val cv = binding.cv


        fun render(viewModel: DeleteRoomViewModel){
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


            // Estos dos botónes son para llamar a un alert dialog para avisar si queremos borrar o no.

            binding.cv.setOnClickListener {
                confirmDialogAlert(it.context, viewModel)
            }
            binding.deleteButton.setOnClickListener {
                confirmDialogAlert(it.context, viewModel)
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

        // Este método llama al dialog en el cual nos avisa, de lo que vamos a hacer y que si estamos seguros,
        // en este caso si queremos seguir, lanza el flag, y borra el aula cuyo puerta es el dato que se obtiene
        // al hacer click sobre dicho element en el recyclerview.
        private fun confirmDialogAlert(it: Context,viewModel: DeleteRoomViewModel) {
            val builder = AlertDialog.Builder(it)
            val view = LayoutInflater.from(it).inflate(R.layout.custom_layout_dialog,null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()

            dialog.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                binding.cv.visibility = View.GONE
                viewModel.deleteRoom(door.text.toString())
                viewModel.setFlag()
                dialog.dismiss()
                viewModel.setDeletedDoor(door.text.toString())
                viewModel.deleteFlag()
            }

            dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.room_item,parent,false)
        return MyViewHolder(itemView)
    }

    // En este método se insertan los valores de la lista en el recyclerview.
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