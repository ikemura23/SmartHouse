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

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the PeripheralManager
 * For example, the snippet below will open a GPIO pin and set it to HIGH:
 *
 * val manager = PeripheralManager.getInstance()
 * val gpio = manager.openGpio("BCM6").apply {
 *     setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * }
 * gpio.value = true
 *
 * You can find additional examples on GitHub: https://github.com/androidthings
 */
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
                // snapshotValue.forEach { (key, value) ->
                //     Log.d(TAG, "key:$key, value:$value")
                // }
                val energies: List<Energy> = snapshotValue.map { Energy(date = it.key, electric = it.value) }.toList()
                Log.d(TAG, energies.toString())
                bindToNowEnergyView(energies.first())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, error.message)
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
