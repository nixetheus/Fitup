package it.polimi.mobile.design

import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
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
import it.polimi.mobile.design.enum.ExerciseType.*
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

        binding.closeModifyExercise.setOnClickListener {
            binding.execiseChangeMenuCard.visibility = View.GONE
        }

        binding.bufferInputValue.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.action == KeyEvent.ACTION_DOWN) {
                binding.confirmAddWorkoutBtn.performClick()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
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
        binding.confirmAddWorkoutBtn.isClickable = true
        binding.addExerciseToWorkoutCard.visibility = View.VISIBLE
        val openAddExerciseMenuAnimation = TranslateAnimation(
            0F, 0F, binding.addExerciseToWorkoutCard.height.toFloat(), 0F)
        openAddExerciseMenuAnimation.duration = 500
        openAddExerciseMenuAnimation.fillAfter = true
        binding.addExerciseToWorkoutCard.startAnimation(openAddExerciseMenuAnimation)
    }

    private fun hideAddExercise() {
        binding.confirmAddWorkoutBtn.isClickable = false
        binding.addExerciseToWorkoutCard.visibility = View.GONE
        val closeAddExerciseMenuAnimation = TranslateAnimation(
            0F, 0F, 0F, binding.addExerciseToWorkoutCard.height.toFloat() + 15.toPx())
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
        binding.kcalOfWorkout.text = editableWorkout.caloriesBurned.toString()
        binding.expOfWorkout.text = editableWorkout.gainedExperience.toString()
        binding.bpmOfWorkout.text = editableWorkout.averageBpmValue.toString()
    }

    private fun onAddNewExercise() {

        if (editableWorkout.userId == helperDB.getFirebaseAuth().uid) {
            val id = helperDB.exercisesSchema.push().key!!
            val exercise = binding.exercisesSpinner.selectedItem as Exercise
            val workoutExercise = createWorkoutExercise(id, editableWorkout, exercise)

            updateWorkoutInfo(exercise, workoutExercise, true)

            // Add to join schema and change UI
            helperDB.workoutsExercisesSchema.child(id).setValue(workoutExercise)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
                    retrieveExercises()
                }
        } else {
            Toast.makeText(this, "You can't modify this workout", Toast.LENGTH_SHORT).show()
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
        binding.noExercisesLayout.visibility = if (workoutExercises.isNotEmpty()) View.GONE else View.VISIBLE
        for (exercise in workoutExercises) {

            val exerciseFragment = FragmentExerciseInWorkoutBinding.inflate(layoutInflater)

            helperDB.exercisesSchema.child(exercise.exerciseId!!).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    exerciseFragment.workoutExercisesList.background = ColorDrawable(HelperFunctions()
                        .getExerciseBackground(dataSnapshot.getValue(Exercise::class.java)!!.type!!, resources, applicationContext))
                }
            }

            with(exerciseFragment) {
                exerciseNameWorkout.text = exercise.exerciseName.toString()
                repsValue.text = exercise.reps.toString()
                setsValue.text = exercise.sets.toString()
                restValue.text = exercise.rest?.let {
                    HelperFunctions().secondsToFormatString(it)
                }
                weightValue.text = exercise.weight.toString()
                bufferValue.text = exercise.buffer.toString()

                setDeleteMenuAnimation(workoutExercisesList, exercise)
            }

            binding.workoutExercisesList.addView(exerciseFragment.root)
        }
    }

    private fun setDeleteMenuAnimation(exerciseCard: View, exercise: WorkoutExercise) {
        exerciseCard.setOnLongClickListener {
            binding.execiseChangeMenuCard.visibility = View.VISIBLE
            binding.exerciseName.text = exercise.exerciseName!!.uppercase()
            val animate = TranslateAnimation(0F, 0F, binding.execiseChangeMenuCard.height.toFloat(), 0F)
            animate.duration = 500
            animate.fillAfter = true
            binding.deleteExerciseL.startAnimation(animate)
            binding.deleteExerciseWorkoutButton.setOnClickListener {
                onDeleteExerciseFromWorkout(exercise)
            }
            false
        }
    }

    private fun onDeleteExerciseFromWorkout(workoutExercise: WorkoutExercise) {
        binding.execiseChangeMenuCard.visibility = View.GONE

        if (editableWorkout.userId == helperDB.getFirebaseAuth().uid) {
            helperDB.workoutsExercisesSchema.child(workoutExercise.id!!).removeValue()
            Toast.makeText(this, "Exercise Eliminated", Toast.LENGTH_SHORT).show()

            helperDB.exercisesSchema.child(workoutExercise.exerciseId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            updateWorkoutInfo(
                                dataSnapshot.getValue(Exercise::class.java)!!,
                                workoutExercise,
                                false
                            )
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        } else {
            Toast.makeText(this, "You can't modify this workout", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateWorkoutInfo(exercise: Exercise, workoutExercise: WorkoutExercise, add: Boolean) {

        if (editableWorkout.workoutId == null) {
            return
        }

        val caloriesPerRep = exercise.caloriesPerRep ?: 0f
        val experiencePerRep = exercise.experiencePerReps ?: 0f
        val reps = workoutExercise.reps ?: 0
        val sets = workoutExercise.sets ?: 0

        val multiplier = if (add) 1 else -1

        val exerciseTypeIndex = when(exercise.type!!.ordinal) {
            in CHEST.ordinal..TRICEPS.ordinal    -> 0
            in ABDOMINALS.ordinal..OBLIQUES.ordinal    -> 1
            in QUADRICEPS.ordinal..CALVES.ordinal    -> 2
            in YOGA.ordinal..YOGA.ordinal    -> 3
            else -> 4
        }

        editableWorkout.numberOfExercises = (editableWorkout.numberOfExercises ?: 0) + multiplier
        editableWorkout.caloriesBurned =
            (editableWorkout.caloriesBurned ?: 0f) + (caloriesPerRep * reps * sets * multiplier)
        editableWorkout.gainedExperience =
            (editableWorkout.gainedExperience ?: 0f) + (experiencePerRep * reps * sets * multiplier)
        editableWorkout.exercisesType?.let { types ->
            types[exerciseTypeIndex] = types[exerciseTypeIndex] + multiplier
        }

        editableWorkout.name?.let { helperDB.workoutsSchema
            .child(editableWorkout.workoutId!!).setValue(editableWorkout).addOnSuccessListener {} }
        showWorkoutData()
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}
