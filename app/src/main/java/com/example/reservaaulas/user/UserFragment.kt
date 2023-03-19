package com.example.reservaaulas.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.reservaaulas.R
import com.example.reservaaulas.adapters.ViewPageAdapter
import com.example.reservaaulas.databinding.FragmentUserBinding
import com.example.reservaaulas.roomToEdit.RoomToEditFragmentArgs
import com.example.reservaaulas.roomToEdit.RoomToEditViewModel
import com.example.reservaaulas.roomToEdit.RoomToEditViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator


class UserFragment : Fragment() {

    // Creamos tres variables donde almacenaremos tanto los binding, como el viewmodel y su factory
    private lateinit var binding: FragmentUserBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var viewModelFactory: UserViewModelFactory



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Instanciamos el binding para poder acceder a los elementos del diseño gráfico
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user, container, false)

        // Obtenemos los argumentos que se pasan por el action
        val args = UserFragmentArgs.fromBundle(requireArguments())
        val userEmail = args.userEmail
        val userName = args.userName


        // Instanciamos el viewmodel y su factory
        val application = requireNotNull(this.activity).application
        viewModelFactory = UserViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(UserViewModel::class.java)

        // Enlazamos el binding al viewmodel
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Obtener el table layour del fragmento y el viewpager.
        val tableLayout = binding.tabLayout
        val viewPager2 = binding.viewPager2
        // instanciamos el viewPageAdapter y le pasamos el nombre del usuario y el email
        val adapter = ViewPageAdapter(parentFragmentManager,lifecycle,userEmail,userName)
        // la viewpage le pasamos el adapter
        viewPager2.adapter = adapter

        // Este método permite desplazarse de una ventana a otra.
        TabLayoutMediator(tableLayout,viewPager2){ tab,position ->
            when(position){
                0 -> {
                    tab.setIcon(R.drawable.home)
                }
                1 -> {
                    tab.setIcon(R.drawable.magnifier)
                }
            }
        }.attach()

        // Inflate the layout for this fragment
        return binding.root
    }
}