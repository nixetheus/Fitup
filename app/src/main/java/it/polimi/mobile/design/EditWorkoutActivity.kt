package it.polimi.mobile.design

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import it.polimi.mobile.design.databinding.ActivityEditWorkoutBinding
import it.polimi.mobile.design.databinding.FragmentExerciseInWorkoutBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.entities.WorkoutExercise
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions


class EditWorkoutActivity : AppCompatActivity() {

    private lateinit var editableWorkout: Workout
    private lateinit var binding: ActivityEditWorkoutBinding
    private val helperDB = DatabaseHelper().getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        editableWorkout = HelperFunctions().getSerializableExtra(intent, "Workout")!!

        binding = ActivityEditWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createBindings()
        setupWorkoutUI()
        setupExercisesSpinner()
        retrieveExercises()
        showWorkoutData()

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(applicationContext, WorkoutListActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun createBindings() {

        binding.homeButton.setOnClickListener{
            val intent = Intent(this, CentralActivity::class.java)
            startActivity(intent)
        }

        binding.confirmAddWorkoutBtn.setOnClickListener{
            onAddNewExercise()
        }

        binding.openAddExerciseLayout.setOnClickListener {
            showAddExercise()
        }

        binding.noExerciseButton.setOnClickListener {
            showAddExercise()
        }

        binding.addExerciseWorkoutClose.setOnClickListener {
            hideAddExercise()
        }
    }

    private fun setupWorkoutUI() {

        binding.workoutName.text = editableWorkout.name!!

        editableWorkout.userId?.let { w ->
            helperDB.usersSchema.child(w).get().addOnSuccessListener {
                binding.workoutCreator.text = DatabaseHelper().getUserFromSnapshot(it)!!.username!!.uppercase()
            }
        }
    }

    private fun showAddExercise() {
        binding.addExerciseToWorkoutCard.visibility = View.VISIBLE
        val openAddExerciseMenuAnimation = TranslateAnimation(
            0F, 0F, binding.addExerciseToWorkoutCard.height.toFloat(), 0F)
        openAddExerciseMenuAnimation.duration = 500
        openAddExerciseMenuAnimation.fillAfter = true
        binding.addExerciseToWorkoutCard.startAnimation(openAddExerciseMenuAnimation)
    }

    private fun hideAddExercise() {
        binding.addExerciseToWorkoutCard.visibility = View.GONE
        val closeAddExerciseMenuAnimation = TranslateAnimation(
            0F, 0F, 0F, binding.addExerciseToWorkoutCard.height.toFloat())
        closeAddExerciseMenuAnimation.duration = 500
        closeAddExerciseMenuAnimation.fillAfter = true
        binding.addExerciseToWorkoutCard.startAnimation(closeAddExerciseMenuAnimation)
    }

    private fun setupExercisesSpinner() {
        helperDB.exercisesSchema.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fillExerciseSpinner(helperDB.getExercisesFromSnapshot(snapshot))
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }

    private fun fillExerciseSpinner(exerciseArrayList: ArrayList<Exercise>) {
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, exerciseArrayList)
        binding.exercisesSpinner.adapter = adapter
    }

    private fun showWorkoutData() {

        // TODO: substitute with workout attribute
        /*var exp = 0f
        var kcalTot = 0F
        for(workoutExercise in workoutExerciseList) {
            for (exercise in exerciseArrayList.filter { ex -> ex.eid == workoutExercise.exerciseId})
            {
                exp += (workoutExercise.reps!! * workoutExercise.sets!!) * exercise.experiencePerReps!!
                kcalTot += (workoutExercise.reps * exercise.caloriesPerRep!!) * workoutExercise.sets
            }
        }*/
        binding.kcalOfWorkout.text = "TODO" // kcalTot.toString()
        binding.expOfWorkout.text = "TODO" // exp.toString()
    }

    // Functionalities
    fun onAddNewExercise() {

        val id              = helperDB.exercisesSchema.push().key!!
        val exercise        = binding.exercisesSpinner.selectedItem as Exercise
        val workoutExercise = createWorkoutExercise(id, editableWorkout, exercise)

        // Update workout types
        if (editableWorkout.workoutId != null) {
            editableWorkout.exercisesType?.let { types ->
                exercise.type?.let { types[it.ordinal] = types[it.ordinal] + 1 }
            }
            editableWorkout.name?.let { helperDB.workoutsSchema.child(it).setValue(editableWorkout).addOnSuccessListener {} }
        }

        // Add to join schema and change UI
        helperDB.workoutsExercisesSchema.child(id).setValue(workoutExercise).addOnSuccessListener {
            Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
            retrieveExercises()
        }

        hideAddExercise()
    }

    private fun createWorkoutExercise(id: String, workout: Workout, exercise: Exercise) : WorkoutExercise {

        val workoutId    = workout.workoutId
        val exerciseId   = exercise.eid
        val exerciseName = exercise.name

        val sets         = HelperFunctions().parseIntInput(binding.setsInputValue.text.toString())
        val reps         = HelperFunctions().parseIntInput(binding.repsInputValue.text.toString())
        val rest         = HelperFunctions().parseIntInput(binding.restInputValue.text.toString())
        val weight       = HelperFunctions().parseFloatInput(binding.weightInputValue.text.toString())
        val buffer       = HelperFunctions().parseIntInput(binding.bufferInputValue.text.toString())

        return WorkoutExercise(id, workoutId, exerciseId, exerciseName, sets, reps, rest, weight, buffer)
    }

    private fun retrieveExercises() {
        helperDB.workoutsExercisesSchema.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                showExerciseCards(DatabaseHelper().getWorkoutsExercisesFromSnapshot(snapshot,
                    editableWorkout.workoutId.toString()))
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }

    private fun showExerciseCards(workoutExercises: List<WorkoutExercise>) {

        binding.workoutExercisesList.removeAllViews()
        if (workoutExercises.isNotEmpty()) binding.noExercisesLayout.visibility = View.GONE
        for (exercise in workoutExercises) {

            val exerciseFragment = FragmentExerciseInWorkoutBinding.inflate(layoutInflater)

            exerciseFragment.exerciseNameWorkout.text = exercise.exerciseName.toString()
            exerciseFragment.repsValue.text = exercise.reps.toString()
            exerciseFragment.setsValue.text = exercise.sets.toString()
            exerciseFragment.restValue.text = exercise.rest?.let {
                HelperFunctions().secondsToFormatString(it)
            }
            exerciseFragment.weightValue.text = exercise.weight.toString()
            exerciseFragment.bufferValue.text = exercise.buffer.toString()

            binding.workoutExercisesList.addView(exerciseFragment.root)
        }
    }

}
