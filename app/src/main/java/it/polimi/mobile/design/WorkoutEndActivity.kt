package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import it.polimi.mobile.design.databinding.ActivityWorkoutEndBinding
import it.polimi.mobile.design.helpers.HelperFunctions

class WorkoutEndActivity : AppCompatActivity() {

    private lateinit var binding:ActivityWorkoutEndBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutEndBinding.inflate(layoutInflater)
        setContentView(binding.root)
        displayResults()
    }

    private fun displayResults() {

        val exp         = HelperFunctions().getExtra<Float>(intent, "Exp")!!
        val nExercises  = HelperFunctions().getExtra<Int>(intent, "N")!! + 1
        val elapsedTime = HelperFunctions().getExtra<Long>(intent, "Time")!!

        binding.expRecapValue.text = exp.toString()
        binding.timeRecapValue.text = formatTime(SystemClock.elapsedRealtime() - elapsedTime)
        binding.exercisesRecapValue.text= nExercises.toString()

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