package it.polimi.mobile.design

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.databinding.ActivityStatsBinding
import it.polimi.mobile.design.entities.DataPoint
import it.polimi.mobile.design.helpers.DatabaseHelper
import java.time.LocalDateTime

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
}