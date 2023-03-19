package com.example.reservaaulas.myReserves

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reservaaulas.R
import com.example.reservaaulas.databinding.FragmentMyReservesBinding
import com.example.reservaaulas.deleteRoom.DeleteRoomAdapter
import com.example.reservaaulas.deleteRoom.DeleteRoomFragmentDirections
import com.example.reservaaulas.entities.Room
import com.example.reservaaulas.login.LoginFragmentDirections
import com.example.reservaaulas.user.UserFragmentArgs
import com.example.reservaaulas.user.UserFragmentDirections
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class MyReservesFragment(private val userEmail: String,private val userName: String) : Fragment() {

    // Creamos tres variables donde almacenaremos tanto los binding, como el viewmodel y su factory

    private lateinit var binding: FragmentMyReservesBinding
    private lateinit var viewModel: MyReservesViewModel
    private lateinit var viewModelFactory: MyReserversViewModelFactory

    private lateinit var roomRecyclerView: RecyclerView
    private lateinit var roomArrayList: ArrayList<Room>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Instanciamos el binding para poder acceder a los elementos del diseño gráfico
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_reserves, container, false)

        // Obtenemos el email por paramentro en este fragmento y lo guardamos en una variable.
        var email = userEmail

        // Instanciamos el viewmodel y su factory
        val application = requireNotNull(this.activity).application
        viewModelFactory = MyReserversViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(MyReservesViewModel::class.java)

        // Enlazamos el binding al viewmodel
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Le damos un valor al text view de la vistga con el nombre del usuario conectado.
        binding.textView.text = "Registered as: " + userName

        // En esta corrutina se obtiene la imagen del usuario, y la guarda en el viewModel
        lifecycleScope.launch {
            val img = viewModel.getUserImage(email)
            img.downloadUrl.addOnSuccessListener {
                viewModel.setUriImage(it)
            }
        }

        // Este observador comprueba que se ha modificado la uri de la image para mostrarla en la vista
        viewModel.uriImage.observe(viewLifecycleOwner, Observer {
            Glide.with(this).load(viewModel.uriImage.value).into(binding.circleImage)
        })

        // Guardamos el el viewModel en nombre del usuario y el email.
        viewModel.setUserEmail(email)
        viewModel.setUserName(userName)


        // Instanciamos el recyclerView
        roomRecyclerView = binding.reservedRoomRecycleview
        roomRecyclerView.layoutManager = LinearLayoutManager(activity)
        roomRecyclerView.setHasFixedSize(true)
        // Instanciamos la lista pertinente al recyclerView
        roomArrayList = arrayListOf<Room>()

        // Esta corrutina obtiene desde la base de datos todas las reservas realizadas de un usuario determinado.
        lifecycleScope.launch {
            viewModel.getReservedRoomsByTheUser()
        }

        // Este observador, comprueba que si ha habido una modificación en la lista de aulas, si es así pues refresca
        // el recyclerview y muestra las nuevas reservas.
        viewModel.rooms.observe(viewLifecycleOwner, Observer {
            if(it != null){
                roomRecyclerView.adapter = MyReservesAdapter(it,viewModel)
            }
        })

        // Este observador, observa si se ha lanzado esta bandera, si es así pues se vuelve a llamar a la base de datos
        // para obtener las reservas de un usuario determinado.
        viewModel.flag.observe(viewLifecycleOwner, Observer {
            if (it != null){
                lifecycleScope.launch {
                    viewModel.getReservedRoomsByTheUser()
                }
            }
        })

        // Este observador observa la bandera
        viewModel.leavingFlag.observe(viewLifecycleOwner, Observer {
            if (it != null){
                Navigation.findNavController(requireView()).navigate(UserFragmentDirections.actionUserFragmentToLoginFragment())
            }
        })

        binding.imageButton.setOnClickListener {
            confirmDialogAlert(it.context,viewModel)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun showSnackBar(view: View,message: String,color: Int) {
        val err = message
        Snackbar.make(view,err, Snackbar.LENGTH_LONG)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
            .setDuration(2000)
            .setBackgroundTint(color) // Color.parseColor("#006400")
            .show()
    }

    private fun confirmDialogAlert(it: Context, viewModel: MyReservesViewModel) {
        val builder = AlertDialog.Builder(it)
        val view = LayoutInflater.from(it).inflate(R.layout.custom_layout_dialog2,null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        dialog.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            // call delete reserve function from viewModel
            viewModel.setLeavingFlag()
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
    }

}