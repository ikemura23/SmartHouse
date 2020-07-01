package com.ikemura.smarthouse

import android.os.Parcelable
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.parcel.Parcelize

class EnergyRepository {
    private var databaseReference: DatabaseReference

    init {
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("electricity")
    }

    fun load() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val snapshotValue: HashMap<String, String> = snapshot.value as HashMap<String, String>
                snapshotValue.forEach { (key, value) ->
                    Log.d(TAG, "key:$key, value:$value")
                }
                val energies: List<Energy> = snapshotValue.map { Energy(date = it.key, electric = it.value) }.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, error.message)
            }
        })
    }

    companion object {
        private val TAG = EnergyRepository::class.java.simpleName
    }
}

@Parcelize
data class Energy(
    val date: String,
    val electric: String
) : Parcelable
