package it.polimi.mobile.design

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.polimi.mobile.design.databinding.ActivityEditWorkoutBinding
import it.polimi.mobile.design.entities.Workout

class EditWorkoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditWorkoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var workout= intent.extras?.get("workout") as Workout
        binding.exerciseNameExampleWorkout.text=workout.name
        binding.addExerciseToWorkoutCard
    }
}