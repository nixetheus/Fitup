package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import it.polimi.mobile.design.databinding.ActivityWorkoutEndBinding
import it.polimi.mobile.design.databinding.ActivityWorkoutPlayBinding

class WorkoutEndActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWorkoutEndBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWorkoutEndBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var exp= intent.extras?.get("exp") as Float
        var n= intent.extras?.get("number of exercises") as Int
        var chronometer= intent.extras?.get("time") as Long
        binding.expRecapValue.text = exp.toString()
        val time = SystemClock.elapsedRealtime() - chronometer
        val h = (time / 3600000).toInt()
        val m = (time - h * 3600000).toInt() / 60000
        val s = (time - h * 3600000 - m * 60000).toInt() / 1000
        val t =
            (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
        binding.timeRecapValue.text = t
        binding.exercisesRecapValue.text=(n+1).toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CentralActivity::class.java)
        startActivity(intent)
    }
}