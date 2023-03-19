package com.example.reservaaulas.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.reservaaulas.myReserves.MyReservesFragment
import com.example.reservaaulas.searchRooms.SearchRoomsFragment
import com.example.reservaaulas.user.UserFragment

class ViewPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,private val userEmail: String,private val userName: String): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }
    // Este viewPageAdapter sirve para transitar de una pestaña a otra en un mismo fragmento.
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                MyReservesFragment(userEmail,userName)
            }
            1 -> {
                SearchRoomsFragment(userEmail,userName)
            }
            else -> {
                Fragment()
            }
        }
    }

}