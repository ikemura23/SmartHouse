package com.ikemura.smarthouse

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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

        // グラフの設定
        setupChart()

        // 取得開始
        load()
    }

    private fun setupChart() {
        binding.chart.apply {
            // setDrawGridBackground(true)
            description.isEnabled
            isScaleXEnabled = false
            setPinchZoom(false)
            setDrawGridBackground(false)

            // データラベルの表示
            legend.apply {
                form = Legend.LegendForm.LINE
                textSize = 11f
                textColor = Color.WHITE
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            // y軸右側の設定
            axisRight.isEnabled = false

            // X軸表示
            xAxis.apply {
                setDrawLabels(false)
                // 格子線を表示する
                setDrawGridLines(false)
            }

            // y軸左側の表示
            axisLeft.apply {
                // y軸の文字色
                textColor = Color.GRAY
                // 格子線を表示する
                setDrawGridLines(false)
            }
        }
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
                bindToNowEnergyView(energies.first())
                bindToGraph(energies)
            }

            override fun onCancelled(error: DatabaseError) {
                // Log.e(TAG, error.message)
                error.toException().printStackTrace()
            }
        })
    }

    /**
     * データをグラフに表示する
     */
    private fun bindToGraph(energies: List<Energy>) {
        Log.d(TAG, energies.toString())
        val entries = energies.mapIndexed { i, energy ->
            Entry(i.toFloat(), energy.electric.toFloat())
        }
            .slice(0..20)
        Log.d(TAG, energies.size.toString())

        val lineDataSet = LineDataSet(entries, "電力").apply {
            // グラフ内の値の丸い点のカラー
            setCircleColor(ContextCompat.getColor(this@MainActivity, R.color.orange))
            // グラフ内の点の上の値の文字色
            valueTextColor = Color.YELLOW
        }
        val dataSets = ArrayList<ILineDataSet>().apply { add(lineDataSet) }
        val lineData = LineData(dataSets)
        binding.chart.data = lineData
        binding.chart.notifyDataSetChanged()
        binding.chart.invalidate()
    }

    /**
     * 瞬間電力の表示
     */
    private fun bindToNowEnergyView(energy: Energy) {
        binding.energy.text = "${energy.electric} w"
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
