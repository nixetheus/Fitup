package it.polimi.mobile.design

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityStatsBinding
import it.polimi.mobile.design.entities.DataPoint
import it.polimi.mobile.design.entities.Graph
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.enum.GraphType
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding

    private var firebaseAuth = FirebaseAuth.getInstance()
    private val databaseHelperInstance = DatabaseHelper().getInstance()
    private var pointsDatabase = FirebaseDatabase.getInstance().getReference("Points")
    private var graphsDatabase = FirebaseDatabase.getInstance().getReference("Graphs")

    private var currentGraph: Graph = Graph()

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

        binding.addDataBtn.setOnClickListener{
            binding.addDataCard.visibility = View.VISIBLE
            val animate = TranslateAnimation(0F, 0F, binding.addDataCard.height.toFloat(), 0F)

            animate.duration = 500
            animate.fillAfter = true
            binding.addDataCard.startAnimation(animate)
            binding.addDataBtn.isClickable = false
            binding.addDataClose.isClickable = true
        }

        binding.addDataClose.setOnClickListener{
            val animate = TranslateAnimation(0F, 0F, 0F, binding.addDataCard.height.toFloat())
            animate.duration = 500
            animate.fillAfter = true
            binding.addDataCard.startAnimation(animate)
            binding.addDataBtn.isClickable = true
            binding.addDataClose.isClickable = false
        }

        binding.confirmAddDataBtn.setOnClickListener{ createData() }
    }

    private fun createData(){

        val uId = firebaseAuth.uid.toString()
        val pId = pointsDatabase.push().key!!
        val value = HelperFunctions().parseFloatInput(binding.dataInputValue.text.toString())
        // TODO: date selector

        if (value > 0) {
            val dataPoint = DataPoint(uId, pId, currentGraph.graphId, LocalDateTime.now()
                .toEpochSecond(ZoneOffset.UTC), value)
            pointsDatabase.child(pId).setValue(dataPoint).addOnSuccessListener {
                val intent = Intent(this, StatsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        else Toast.makeText(this, "Fill in all fields to continue!!", Toast.LENGTH_SHORT).show()

    }

    private fun displayData() {

        // Display Graph Data
        graphsDatabase.get().addOnSuccessListener { graphsSnapshot ->
            val graph =
                DatabaseHelper().getGraphsFromSnapshot(graphsSnapshot)
                    .filter { it.graphId == currentGraph.graphId}[0]
            binding.graphName.text = graph.graphName
        }

        // Display Data Points
        pointsDatabase.get().addOnSuccessListener { pointsSnapshot ->
            var points = databaseHelperInstance!!.getPointsFromSnapshot(pointsSnapshot)
            points = points.filter { it.graphId == currentGraph.graphId}.sortedBy { it.xcoordinate } // TODO: userId too
            Log.w("Data:", points.toString())
            binding.graphVisualizer.dataPoints = points
            binding.graphVisualizer.drawDataPoints()

            if (points.isNotEmpty()) {

                val latest = points.last().ycoordinate.toString() + currentGraph.graphMeasure
                binding.latestPointValue.text = latest

                binding.differenceValue.text =
                    if (points.size == 1) "0.0" + currentGraph.graphMeasure
                    else (points.last().ycoordinate!! - points[points.size - 2]
                        .ycoordinate!!).toString() + currentGraph.graphMeasure
            } else {
                binding.latestPointValue.text = resources.getString(R.string.null_value)
                binding.differenceValue.text = resources.getString(R.string.null_value)
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
                changeSubSpinner(binding.mainGraphSpinner.selectedItemPosition)
            }
        }

        // Sub Spinner
        changeSubSpinner(binding.mainGraphSpinner.selectedItemPosition)
    }

    private fun changeSubSpinner(index: Int) {
        graphsDatabase.get().addOnSuccessListener { graphsSnapshot ->

            val graphs =
                DatabaseHelper().getGraphsFromSnapshot(graphsSnapshot).filter { it.graphType == index}
            val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, graphs)
            binding.secondaryGraphSpinner.adapter = adapter
            graphs[0].graphId?.let {
                currentGraph = graphs[0]
                displayData()
            }

            binding.secondaryGraphSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    graphs[binding.secondaryGraphSpinner.selectedItemPosition].graphId?.let {
                        currentGraph = graphs[binding.secondaryGraphSpinner.selectedItemPosition]
                        displayData()
                    }
                }
            }
        }
    }
}