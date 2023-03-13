package it.polimi.mobile.design

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityStatsBinding
import it.polimi.mobile.design.entities.DataPoint
import it.polimi.mobile.design.enum.GraphType
import it.polimi.mobile.design.helpers.DatabaseHelper
import java.time.LocalDateTime
import java.util.*


class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding

    private val databaseHelperInstance = DatabaseHelper().getInstance()
    private var pointsDatabase = FirebaseDatabase.getInstance().getReference("Points")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBindings()
        setupSpinners()
    }

    private fun setupBindings() {

        // TEST
        val pointId = "Mw19C3PhigZGoG1OtwpTt8BF1op1"
        binding.addDataBtn.setOnClickListener {
            pointsDatabase.child(pointId).get().addOnSuccessListener { pointsSnapshot ->
                //val points = databaseHelperInstance!!.getPointsFromSnapshot(pointsSnapshot)
                val points = listOf(DataPoint(), DataPoint(yCoordinate = 20f, xCoordinate = LocalDateTime.MAX),
                    DataPoint(yCoordinate = 15f, xCoordinate = LocalDateTime.now()))
                binding.graphVisualizer.dataPoints = points.sortedBy { it.xCoordinate }
                binding.graphVisualizer.drawDataPoints()
            }
        }
    }

    private fun setupSpinners() {

        // Main Spinner
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, GraphType.values())
        binding.mainGraphSpinner.adapter = adapter
        binding.mainGraphSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                changeSubSpinner()
            }
        }

        // Sub Spinner
    }

    private fun changeSubSpinner() {

    }
}