package it.polimi.mobile.design

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.polimi.mobile.design.databinding.ActivityStatsBinding
import it.polimi.mobile.design.entities.DataPoint
import it.polimi.mobile.design.entities.Graph
import it.polimi.mobile.design.enum.GraphType
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding

    private var firebaseAuth = FirebaseAuth.getInstance()
    private val helperDB = DatabaseHelper().getInstance()

    private val myCalendar : Calendar = Calendar.getInstance()
    private var currentGraph: Graph = Graph()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBindings()
        setupSpinners()
        setupDatePicker()
        binding.graphScroll.scrollTo(binding.graphScroll.scrollX, 0)
    }

    private fun setupBindings() {

        binding.homeButton.setOnClickListener{
            val intent = Intent(this, CentralActivity::class.java)
            startActivity(intent)
        }

        binding.addDataBtn.setOnClickListener{
            toggleAddDataVisibility(true)
        }

        binding.noDataButton.setOnClickListener{
            toggleAddDataVisibility(true)
        }

        binding.addDataClose.setOnClickListener{
            toggleAddDataVisibility(false)
        }

        binding.confirmAddDataBtn.setOnClickListener{ createData() }
    }

    private fun toggleAddDataVisibility(show: Boolean) {
        binding.addDataCard.visibility = if (show) View.VISIBLE else View.GONE

        val translateY = if (show) 0F else binding.addDataCard.height.toFloat() + 15.toPx()
        binding.addDataCard.animate().translationY(translateY).setDuration(500).start()

        binding.addDataBtn.isClickable = !show
        binding.addDataClose.isClickable = show
    }


    private fun createData() {

        val uId = firebaseAuth.uid.toString()
        val pId = helperDB.pointsSchema.push().key!!
        val value = HelperFunctions().parseFloatInput(binding.dataInputValue.text.toString())
        val dateValue = binding.dateInputValue.text.toString()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        val dt = LocalDate.parse(dateValue, formatter)

        if (value > 0) {
            val dataPoint = DataPoint(uId, pId, currentGraph.graphId,
                ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), dt) * 86400, value)
            helperDB.pointsSchema.child(pId).setValue(dataPoint).addOnSuccessListener {
                displayData()
                binding.addDataBtn.isClickable = true
            }
            binding.addDataCard.visibility = View.INVISIBLE
        }
        else Toast.makeText(this, "Fill in all fields to continue!!", Toast.LENGTH_SHORT).show()

    }

    private fun displayData() {

        binding.dataInputLabel.text = currentGraph.graphMeasure

        // Display Graph Data
        helperDB.graphsSchema.get().addOnSuccessListener { graphsSnapshot ->
            val graph =
                DatabaseHelper().getGraphsFromSnapshot(graphsSnapshot)
                    .filter { it.graphId == currentGraph.graphId}[0]
            binding.graphName.text = graph.graphName
        }

        // Display Data Points
        helperDB.pointsSchema.get().addOnSuccessListener { pointsSnapshot ->
            var points = helperDB.getPointsFromSnapshot(pointsSnapshot)
            points = points.filter { it.graphId == currentGraph.graphId}.sortedBy { it.xcoordinate }
            Log.w("Data:", points.toString())
            binding.graphVisualizer.dataPoints = points
            binding.graphVisualizer.unitOfMeasure = currentGraph.graphMeasure!!
            binding.graphVisualizer.drawDataPoints()
            binding.graphScroll.fullScroll(ScrollView.FOCUS_RIGHT)

            if (points.isNotEmpty()) {

                val latest = points.last().ycoordinate.toString()
                binding.latestPointValue.text = latest
                binding.latestPointMeasure.text = currentGraph.graphMeasure

                binding.differenceValue.text =
                    if (points.size == 1) "0.0"
                    else (points.last().ycoordinate!! - points[points.size - 2]
                        .ycoordinate!!).format(1)
                binding.differenceValueMeasure.text = currentGraph.graphMeasure

                binding.noDataLayout.visibility = View.GONE
                binding.graphCard.visibility = View.VISIBLE
            } else {
                binding.latestPointValue.text = resources.getString(R.string.null_value)
                binding.differenceValue.text = resources.getString(R.string.null_value)
                binding.latestPointMeasure.text = ""
                binding.differenceValueMeasure.text = ""
                binding.noDataLayout.visibility = View.VISIBLE
                binding.graphCard.visibility = View.GONE
            }
        }
    }

    private fun Float.format(digits: Int) = "%.${digits}f".format(this)

    private fun setupSpinners() {

        // Main Spinner
        val adapter = ArrayAdapter(this, R.layout.fragment_spinner_item, GraphType.values())
        adapter.setDropDownViewResource(R.layout.fragment_spinner_dropdown_item)
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

        helperDB.graphsSchema.get().addOnSuccessListener { graphsSnapshot ->

            val graphs =
                DatabaseHelper().getGraphsFromSnapshot(graphsSnapshot).filter { it.graphType == index}
                    .toMutableList()

            val itemWithHighestPriority = graphs.find { it.graphName == "Body Weight" }
            if (itemWithHighestPriority != null) {
                graphs.remove(itemWithHighestPriority)
                graphs.add(0, itemWithHighestPriority)
            }

            val adapter = ArrayAdapter(this, R.layout.fragment_spinner_item, graphs)
            adapter.setDropDownViewResource(R.layout.fragment_spinner_dropdown_item)
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

    private fun setupDatePicker() {

        val dateInput = binding.dateInputValue

        val myFormat = "dd/MM/yy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.dateInputValue.setText(dateFormat.format(Date()))

        val date =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }
        dateInput.setOnClickListener {
            DatePickerDialog(
                this@StatsActivity,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.dateInputValue.setText(dateFormat.format(myCalendar.time))
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}