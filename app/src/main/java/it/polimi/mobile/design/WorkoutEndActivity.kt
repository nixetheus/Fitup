package it.polimi.mobile.design

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import it.polimi.mobile.design.databinding.ActivityWorkoutEndBinding
import it.polimi.mobile.design.helpers.HelperFunctions
import kotlin.math.roundToInt

class WorkoutEndActivity : AppCompatActivity() {

    private lateinit var binding:ActivityWorkoutEndBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutEndBinding.inflate(layoutInflater)
        setContentView(binding.root)
        displayResults()
    }

    @SuppressLint("SetTextI18n")
    private fun displayResults() {


        val exp         = HelperFunctions().getExtra<Float>(intent, "Exp")!!


        val nExercises  = HelperFunctions().getExtra<Int>(intent, "N")!!
        val elapsedTime = HelperFunctions().getExtra<Long>(intent, "Time")!!
        val bpm = HelperFunctions().getExtra<Double>(intent, "bpm")!!

        binding.expRecapValue.text = String.format("%.3f", exp)
        binding.timeRecapValue.text = formatTime(SystemClock.elapsedRealtime() - elapsedTime)
        binding.exercisesRecapValue.text= nExercises.toString()
        if (!bpm.isNaN())
            binding.bpmAvgValue.text=bpm.roundToInt().toString()
        else binding.bpmAvgValue.text="not available"

    }

    private fun formatTime(time: Long) :  String {
        val h = (time / 3600000).toInt()
        val m = (time - h * 3600000).toInt() / 60000
        val s = (time - h * 3600000 - m * 60000).toInt() / 1000
        return (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
    }

    // TODO: modernize
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CentralActivity::class.java)
        startActivity(intent)
    }
}