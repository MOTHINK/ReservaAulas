package com.example.reservaaulas.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.reservaaulas.R
import com.example.reservaaulas.entities.PermanentReserve
import com.example.reservaaulas.entities.Reserve
import com.example.reservaaulas.entities.Room
import com.example.reservaaulas.entities.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.File
import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.util.*

class Database() {

    private val db =  FirebaseFirestore.getInstance()

    private var ref: FirebaseStorage = FirebaseStorage.getInstance()

    fun saveUserIntoDatabase(user: User) {
        db.collection("users").document(user.email).set(
            hashMapOf(
                "name" to user.name,
                "email" to user.email,
                "password" to user.password,
                "avatar" to user.avatar,
                "admin" to user.admin
            )
        )
    }


    fun saveImageIntoDatabase(filename: String,imageUri: Uri){
        val newRef = ref.getReference("/images/$filename")
        newRef.putFile(imageUri).addOnSuccessListener {
            println("Se ha insertado la imagen correctamente")
        }
    }

    suspend fun getImageByEmail(email: String): StorageReference {
        val user = getUserByEmail(email)
        val newRef = ref.reference.child("images/${user!!.avatar}")
        return newRef
    }

    suspend fun getUserByEmail(email: String): User? {
//        var user:User? = null
        var user = User("","","","")
        db.collection("users").document(email).get().addOnSuccessListener {
            if(it.exists()){
                user = it.toObject(User::class.java)!!
            }
        }.await()
        return user
    }

    fun saveAllRoomInDb(doors: Array<String>){
        var list = doors
        for(i in 0..15){
            db.collection("doors").document(list[i]).set(
                hashMapOf(
                    "door" to list[i],
                    "designed" to false
                )
            )
        }
    }

    suspend fun getAvailableDoors(): List<String> {
        var doorsList = mutableListOf<String>()
        db.collection("doors").whereEqualTo("designed", false).get().addOnSuccessListener { doors ->
            for(door in doors){
                doorsList.add(door.get("door").toString())
            }
        }.await()
        doorsList.add(0,"None")
        return doorsList
    }

    suspend fun getAvailableDoorsToEdit(door: String): List<String> {
        var doorsList = mutableListOf<String>()
        db.collection("rooms").whereNotEqualTo("door", door).get().addOnSuccessListener { doors ->
            for(door in doors){
                doorsList.add(door.get("door").toString())
                println("Obtenido " + door.get("door"))
            }
        }.await()
        doorsList.remove(door)
        doorsList.add(0,"None")
        return doorsList
    }


    suspend fun getUsedDoors(): List<String> {
        var doorsList = mutableListOf<String>()
        db.collection("doors").whereEqualTo("designed", true).get().addOnSuccessListener { doors ->
            for(door in doors){
                doorsList.add(door.get("door").toString())
            }
        }.await()
        doorsList.add(0,"All")
        return doorsList
    }

    fun designDoor(door: String) {
        db.collection("doors").document(door).set(
            hashMapOf(
                "door" to door,
                "designed" to true
            )
        )
    }

    fun undesignRoom(door: String){
        db.collection("doors").document(door).set(
            hashMapOf(
                "door" to door,
                "designed" to false
            )
        )
    }

    fun saveRoom(room: Room){
        db.collection("rooms").document(room.door).set(
            hashMapOf(
                "modality" to room.modality,
                "door" to room.door,
                "nSeats" to room.nSeats
            )
        )
    }


    suspend fun getAllRooms(): MutableList<Room> {
        var roomList = mutableListOf<Room>()
        db.collection("rooms").get().addOnSuccessListener { rooms ->
            for(room in rooms) {
                val r = Room(room.get("modality").toString(),room.get("door").toString(), room.get("nSeats").toString().toInt())
                roomList.add(r)
            }
        }.await()
        return roomList
    }

    fun deleteRoom(door: String) {
        db.collection("rooms").document(door).delete()
    }


    suspend fun getRoomToEdit(door: String): Room {
        var room: Room = Room("","",0)
        db.collection("rooms").document(door).get().addOnSuccessListener {
            if(it.exists()){
                room.modality = it.get("modality").toString()
                room.door = it.get("door").toString()
                room.nSeats = it.get("nSeats").toString().toInt()
            }
        }.await()
        return room
    }

    suspend fun checkRoomExists(door: String): Boolean {
        var exists = false
        var roomList = mutableListOf<Room>()
        db.collection("rooms").document(door).get().addOnSuccessListener { room ->
            if(room.exists()){
                val r = Room(room.get("modality").toString(),room.get("door").toString(), room.get("nSeats").toString().toInt())
                roomList.add(r)
            }
        }.await()
        if(roomList.size > 0){
            exists = true
        }
        return exists
    }

    suspend fun getRoomByDoor(door: String): MutableList<Room> {
        var roomList = mutableListOf<Room>()
        db.collection("rooms").document(door).get().addOnSuccessListener { room ->
            val r = Room(room.get("modality").toString(),room.get("door").toString(), room.get("nSeats").toString().toInt())
            roomList.add(r)
        }.await()
        return roomList
    }

    suspend fun getRoomsByModalityAndNumOfSeats(modality: String,nSeats: Int): MutableList<Room> {
        var numSeats = 0
        if(nSeats == 0){
            numSeats = 10
        } else {
            numSeats = nSeats
        }

        var roomList = mutableListOf<Room>()
        if(modality == "All"){
            db.collection("rooms").whereGreaterThanOrEqualTo("nSeats",numSeats).get().addOnSuccessListener { rooms ->
                for (room in rooms){
                    val r = Room(room.get("modality").toString(),room.get("door").toString(), room.get("nSeats").toString().toInt())
                    roomList.add(r)
                }
            }.await()
        } else {
            db.collection("rooms").whereEqualTo("modality",modality).get().addOnSuccessListener { rooms ->
                for (room in rooms){
                    if(room.get("nSeats").toString().toInt() >= numSeats){
                        val r = Room(room.get("modality").toString(),room.get("door").toString(), room.get("nSeats").toString().toInt())
                        roomList.add(r)
                    }
                }
            }.await()
        }
        return roomList
    }


    fun saveReserve(reserve: Reserve){
        db.collection("reserves").document(reserve.id).set(
            hashMapOf(
                "door" to reserve.door,
                "email" to reserve.email,
                "day" to reserve.day,
                "month" to reserve.month,
                "year" to reserve.year,
                "hour" to reserve.hour
            )
        )
    }


    suspend fun getAllReserves(day: Int, month: Int, year: Int,door: String): MutableList<Reserve> {
        var reserveList = mutableListOf<Reserve>()
        db.collection("reserves").get().addOnSuccessListener { reserves ->
            for(reserve in reserves) {
                if(reserve.get("day").toString().toInt() == day && reserve.get("month").toString().toInt() == month && reserve.get("year").toString().toInt() == year && reserve.get("door").toString() == door){
                    val r = Reserve(
                        "",
                        reserve.get("door").toString(),
                        reserve.get("email").toString(),
                        reserve.get("day").toString().toInt(),
                        reserve.get("month").toString().toInt(),
                        reserve.get("year").toString().toInt(),
                        reserve.get("hour").toString().toInt()
                    )
                    reserveList.add(r)
                }
            }
        }.await()
        return reserveList
    }


    suspend fun getUserReserves(email: String): MutableList<Reserve> {

        var reserveList = mutableListOf<Reserve>()
        db.collection("reserves").whereEqualTo("email",email).get().addOnSuccessListener { reserves ->
            for(reserve in reserves) {
                val r = Reserve(
                    "",
                    reserve.get("door").toString(),
                    reserve.get("email").toString(),
                    reserve.get("day").toString().toInt(),
                    reserve.get("month").toString().toInt(),
                    reserve.get("year").toString().toInt(),
                    reserve.get("hour").toString().toInt()
                )
                reserveList.add(r)
            }
        }.await()
        return reserveList
    }

    fun deleteReserve(id: String){
        db.collection("reserves").document(id).delete()
    }

    fun deleteReserveByDoor(door: String){
        db.collection("reserves").whereEqualTo("door",door).get().addOnSuccessListener { reserves ->
            for(reserve in reserves){
                db.collection("reserves").document(reserve.id).delete()
            }
        }
    }

    fun savePermanentReserveHour(prhList: MutableList<PermanentReserve>){
        for(prh in prhList){
            var id = prh.door + prh.day + prh.hour.toString()
            db.collection("permanentReserve").document(id).set(
                hashMapOf(
                    "door" to prh.door,
                    "day" to prh.day,
                    "hour" to prh.hour
                )
            )
        }
    }

    suspend fun getPermanentReserveHourByDoor(door: String): MutableList<PermanentReserve> {
        var prhList = mutableListOf<PermanentReserve>()
        db.collection("permanentReserve").whereEqualTo("door",door).get().addOnSuccessListener { prhByDoor ->
            for(prh in prhByDoor){
                val pr = PermanentReserve(prh.get("door").toString(),prh.get("day").toString(),prh.get("hour").toString().toInt())
                prhList.add(pr)
            }
        }.await()
        return prhList
    }

    fun deletePermanentReserveHourByDoor(door: String){
        db.collection("permanentReserve").whereEqualTo("door",door).get().addOnSuccessListener { prhList ->
            for(prh in prhList){
                db.collection("permanentReserve").document(prh.id).delete()
            }
        }
    }


}