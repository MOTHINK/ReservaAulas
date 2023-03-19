package com.example.reservaaulas.searchRooms

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.reservaaulas.R
import com.example.reservaaulas.databinding.ReservedItemBinding
import com.example.reservaaulas.databinding.SearchItemBinding
import com.example.reservaaulas.entities.Room
import com.example.reservaaulas.login.LoginFragmentDirections
import com.example.reservaaulas.myReserves.MyReservesViewModel
import com.example.reservaaulas.user.UserFragmentDirections
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class SearchRoomsAdapter(private val roomList: ArrayList<Room>, private val viewModel: SearchRoomsViewModel)
    : RecyclerView.Adapter<SearchRoomsAdapter.MyViewHolder>()  {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val binding = SearchItemBinding.bind(itemView)

        val modality = binding.TVModality
        val door = binding.tvDoor
        val nSeats = binding.tvNumSeats
        val image = binding.imageIcon
        val cv = binding.cv

        fun render(viewModel: SearchRoomsViewModel){

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
                Navigation.findNavController(it).navigate(UserFragmentDirections.actionUserFragmentToReserveRoomFragment(viewModel.userEmail.value.toString(),door.text.toString(),viewModel.userName.value.toString()))
            }
            binding.searchButton.setOnClickListener {
                Navigation.findNavController(it).navigate(UserFragmentDirections.actionUserFragmentToReserveRoomFragment(viewModel.userEmail.value.toString(),door.text.toString(),viewModel.userName.value.toString()))
            }
        }

        private fun showSnackBar(view: View, message: String, color: Int) {
            val err = message
            Snackbar.make(view,err, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .setDuration(2000)
                .setBackgroundTint(color) // Color.parseColor("#006400")
                .show()
        }

        private fun confirmDialogAlert(it: Context, viewModel: SearchRoomsViewModel) {
            val builder = AlertDialog.Builder(it)
            val view = LayoutInflater.from(it).inflate(R.layout.custom_layout_dialog,null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()

            dialog.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                // call delete reserve function from viewModel
                viewModel.setFlag()
                dialog.dismiss()
            }

            dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchRoomsAdapter.MyViewHolder, position: Int) {
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