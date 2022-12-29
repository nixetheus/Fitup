package it.polimi.mobile.design

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import it.polimi.mobile.design.databinding.ActivityWorkoutPlayBinding
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.entities.WorkoutExercise
import kotlin.properties.Delegates


class WorkoutPlayActivity : AppCompatActivity() {
    private val FORMAT = "%02d:%02d"
    private lateinit var binding:ActivityWorkoutPlayBinding
    private lateinit var chrono: Chronometer
    private lateinit var database: DatabaseReference
    private lateinit var workoutExercise: ArrayList<WorkoutExercise>
    private var i=0

    override fun onCreate(savedInstanceState: Bundle?) {
        val exp =0F
        super.onCreate(savedInstanceState)
        binding=ActivityWorkoutPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        workoutExercise= arrayListOf<WorkoutExercise>()

        chrono= binding.workoutTimeValue
        chrono.onChronometerTickListener =
            OnChronometerTickListener { chronometer ->
                val time = SystemClock.elapsedRealtime() - chronometer.base
                val h = (time / 3600000).toInt()
                val m = (time - h * 3600000).toInt() / 60000
                val s = (time - h * 3600000 - m * 60000).toInt() / 1000
                val t =
                    (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
                chronometer.text = t
            }
        chrono.base = SystemClock.elapsedRealtime()
        chrono.text = "00:00:00"

        var workout= intent.extras?.get("workout") as Workout
        binding.playWorkoutName.text=workout.name
        database=FirebaseDatabase.getInstance().getReference("WorkoutExercise")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutExercise.clear()
                if (snapshot.exists()){
                    for (workSnap in snapshot.children) {
                        val workData = workSnap.getValue(WorkoutExercise::class.java)
                        if (workData != null) {
                            if (workData.workoutId == workout.woId) {

                                    workoutExercise.add(workData!!)
                                }
                            }
                        }
                    }


                }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        binding.beingTimeButton.setOnClickListener{
            start()

        }

        binding.startStopButton.setOnClickListener{
            i=0

            if (workoutExercise.size!=0) {
                binding.currentExerciseName.text = workoutExercise[i].exerciseName
                binding.startCurrentExerciseLayout.visibility= View.VISIBLE
            }
            else {
                Toast.makeText(
                    this,
                    "This workout is empty, please add exercises to continue your training",
                    Toast.LENGTH_SHORT
                ).show()
                chrono.stop()
                binding.startStopButton.text = "FINISH!!"
                binding.startStopButton.setOnClickListener {
                    val intent = Intent(this, CentralActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        binding.nextExerciseButton.setOnClickListener{
            if(i<workoutExercise.size-1){
                i++
                binding.currentExerciseName.text= workoutExercise[i].exerciseId
            }
            else {
                chrono.stop()
                binding.startStopButton.text = "FINISH!!"
                binding.startCurrentExerciseLayout.visibility = View.GONE
                binding.startStopButton.setOnClickListener {
                    val intent = Intent(this, CentralActivity::class.java)
                    startActivity(intent)
                }
            }
        }


    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CentralActivity::class.java)
        startActivity(intent)
        true
    }




    private fun start(){
        chrono.start()

    }


}