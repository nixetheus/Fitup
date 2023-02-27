package it.polimi.mobile.design

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.polimi.mobile.design.databinding.ActivityEditWorkoutBinding
import it.polimi.mobile.design.databinding.FragmentExerciseInWorkoutBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.entities.WorkoutExercise
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions


class EditWorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditWorkoutBinding
    private var exerciseDatabase = FirebaseDatabase.getInstance().getReference("Exercise")
    private var workoutExerciseDatabase = FirebaseDatabase.getInstance().getReference("WorkoutExercise")
    private val databaseHelperInstance = DatabaseHelper().getInstance()

    private var exerciseArrayList = ArrayList<Exercise>()
    private var exerciseInWorkout = ArrayList<Exercise>()
    private var workoutExerciseList = ArrayList<WorkoutExercise>()

    private lateinit var adapter: ArrayAdapter<Exercise>

    private lateinit var workout: Workout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        workout = intent.extras?.get("workout") as Workout  // TODO: with JSON

        binding = ActivityEditWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWorkoutUI()
        setupExercisesUI()

        binding.confirmAddWorkoutBtn.setOnClickListener{onAddNewExercise()}

        workoutExerciseDatabase.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutExerciseList = DatabaseHelper().getWorkoutsExercisesFromSnapshot(snapshot,
                        workout.workoutId.toString())
                showExerciseCards(workoutExerciseList)
                calculateWorkoutData()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })


    }

    private fun setupWorkoutUI() {

        binding.workoutName.text = workout.name

        // Add workout animation
        binding.openAddExerciseLayout.setOnClickListener{
            binding.addExerciseToWorkoutCard.visibility = View.VISIBLE
            val openAddExerciseMenuAnimation = TranslateAnimation(
                0F, 0F, binding.addExerciseToWorkoutCard.height.toFloat(), 0F)
            openAddExerciseMenuAnimation.duration = 500
            openAddExerciseMenuAnimation.fillAfter = true
            binding.addExerciseToWorkoutCard.startAnimation(openAddExerciseMenuAnimation)
        }

        // Close add workout animation
        binding.addWorkoutClose.setOnClickListener{
            binding.addExerciseToWorkoutCard.visibility=View.GONE
            val closeAddExerciseMenuAnimation = TranslateAnimation(
                0F, 0F, 0F, binding.addExerciseToWorkoutCard.height.toFloat())
            closeAddExerciseMenuAnimation.duration = 500
            closeAddExerciseMenuAnimation.fillAfter = true
            binding.addExerciseToWorkoutCard.startAnimation(closeAddExerciseMenuAnimation)

        }
    }

    private fun setupExercisesUI() {
        exerciseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exerciseArrayList = databaseHelperInstance!!.getExercisesFromSnapshot(snapshot)
                showExerciseSpinner()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }

    private fun onAddNewExercise() {

        workoutExerciseDatabase= FirebaseDatabase.getInstance().getReference("WorkoutExercise")

        val id = exerciseDatabase.push().key!!

        val workoutId = workout.workoutId

        val exercise = binding.exercisesSpinner.selectedItem as Exercise
        val exerciseId = exercise.eid
        val exerciseName = exercise.name

        val sets = HelperFunctions().parseIntInput(binding.setsInputValue.text.toString())
        val reps = HelperFunctions().parseIntInput(binding.repsInputValue.text.toString())
        val rest = HelperFunctions().parseIntInput(binding.restInputValue.text.toString())

        val workoutExercise= WorkoutExercise(id, workoutId, exerciseId,
            exerciseName, sets, reps, rest)

        workoutExerciseDatabase = FirebaseDatabase.getInstance().getReference("WorkoutExercise")
        workoutExerciseDatabase.child(id).setValue(workoutExercise).addOnSuccessListener {

            Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()  // TODO: better UI
            binding.addExerciseToWorkoutCard.visibility = View.GONE
            exerciseInWorkout.add(exercise)

            finish()
            startActivity(intent)
        }
    }

    private fun showExerciseSpinner() {
        adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, exerciseArrayList)
        binding.exercisesSpinner.adapter = adapter
    }

    private fun showExerciseCards(workoutExercise: List<WorkoutExercise>) {
        for (exercise in workoutExercise) {

            val exerciseFragment = FragmentExerciseInWorkoutBinding.inflate(layoutInflater)

            exerciseFragment.exerciseNameWorkout.text = exercise.exerciseName.toString()
            exerciseFragment.repsValue.text = exercise.reps.toString()
            exerciseFragment.setsValue.text = exercise.sets.toString()
            exerciseFragment.restValue.text = exercise.rest?.let {
                HelperFunctions().secondsToFormatString(it)
            }

            binding.workoutExercisesList.addView(exerciseFragment.root)
        }
    }

    private fun calculateWorkoutData() {

        var exp = 0f
        var kcalTot = 0F
        for(workoutExercise in workoutExerciseList) {
            for (exercise in exerciseArrayList.filter { ex -> ex.eid == workoutExercise.exerciseId})
            {
                exp += (workoutExercise.reps!! * workoutExercise.sets!!) * exercise.experiencePerReps!!
                kcalTot += (workoutExercise.reps * exercise.caloriesPerRep!!) * workoutExercise.sets
            }
        }
        binding.kcalOfWorkout.text = kcalTot.toString()
        binding.expOfWorkout.text= exp.toString()
    }
}

