package com.ikemura.smarthouse

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ikemura.smarthouse.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Firebaseのセットアップ
        setupDatabase()

        // 取得開始
        load()
    }

    private fun setupDatabase() {
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("electricity")
    }

    private fun load() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val snapshotValue: HashMap<String, String> = snapshot.value as HashMap<String, String>
                val energies: List<Energy> = snapshotValue.map { Energy(date = it.key, electric = it.value) }.toList()
                Log.d(TAG, energies.toString())
                bindToNowEnergyView(energies.first())
            }

            override fun onCancelled(error: DatabaseError) {
                // Log.e(TAG, error.message)
                error.toException().printStackTrace()
            }
        })
    }

    private fun bindToNowEnergyView(energy: Energy) {
        binding.energy.text = energy.electric
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
