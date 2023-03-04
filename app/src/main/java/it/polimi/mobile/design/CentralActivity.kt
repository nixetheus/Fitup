package it.polimi.mobile.design


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutRecentBinding
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.enum.ExerciseType
import it.polimi.mobile.design.helpers.DatabaseHelper


class CentralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCentralBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private val databaseInstance = FirebaseDatabase.getInstance()
    private val databaseHelperInstance = DatabaseHelper().getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityCentralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showUser()
        createBindings()
        workoutsCallback()
    }

    private fun workoutsCallback() {
        val workoutsSchema = databaseInstance.getReference("Workout")
        workoutsSchema.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showWorkouts(
                    databaseHelperInstance!!.getWorkoutsFromSnapshot(snapshot))
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }

    private fun showUser(){
        val usersSchema = databaseInstance.getReference("Users")
        firebaseAuth.uid?.let { userId ->
            usersSchema.child(userId).get().addOnSuccessListener { userSnapshot ->
                val user = databaseHelperInstance!!.getUserFromSnapshot(userSnapshot)
                binding.usernameText.text = user!!.username
                binding.userLevelValue.text = user.exp.toString()
            }
        }
    }

    private fun createBindings() {
        binding.exercisesLink.setOnClickListener{
            val intent = Intent(this, ExerciseListActivity::class.java)
            startActivity(intent)
        }

        binding.statsLink.setOnClickListener{
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        binding.workoutsLink.setOnClickListener{
            val intent = Intent(this, WorkoutListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showWorkouts(workouts: List<Workout>) {

        for (workout in workouts) {
            showRecentWorkout(workout)
            showChosenWorkout(workout)
        }
    }

    private fun showRecentWorkout(workout: Workout) {

        val workoutsLayout = FragmentWorkoutRecentBinding.inflate(layoutInflater)

        workoutsLayout.workoutDisplayName.text = workout.name

        when(workout.exercisesType?.let { getWorkoutType(it) }) {
            0 -> workoutsLayout.colorTypeImage.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.gradient_arms, applicationContext.theme))
            1 -> workoutsLayout.colorTypeImage.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.gradient_legs, applicationContext.theme))
            2 -> workoutsLayout.colorTypeImage.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.gradient_core, applicationContext.theme))
            3 -> workoutsLayout.colorTypeImage.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.gradient_yoga, applicationContext.theme))
            else -> workoutsLayout.colorTypeImage.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.gradient_default, applicationContext.theme))
        }

        binding.workoutsLayoutRecent.addView(workoutsLayout.root)

        workoutsLayout.workoutCard.setOnClickListener {
            val intent = Intent(this, WorkoutPlayActivity::class.java)
            intent.putExtra("workout", workout)
            startActivity(intent)
        }
    }

    private fun showChosenWorkout(workout: Workout) {

        val workoutsLayout = FragmentWorkoutBinding.inflate(layoutInflater)

        workoutsLayout.workoutDisplayName.text = workout.name

        // TODO: real values
        workoutsLayout.exercisesValue.text = getString(R.string.null_value)
        workoutsLayout.kcalValue.text = getString(R.string.null_value)
        workoutsLayout.bpmValue.text = getString(R.string.null_value)

        workoutsLayout.exercisesLabel.text = getString(R.string.number_exercises_label)
        workoutsLayout.kcalLabel.text = getString(R.string.calories_data_label)
        workoutsLayout.bpmLabel.text = getString(R.string.bpm_data_label)

        when(workout.exercisesType?.let { getWorkoutType(it) }) {
            0 -> workoutsLayout.workoutColorLayout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.gradient_arms, applicationContext.theme)
            1 -> workoutsLayout.workoutColorLayout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.gradient_legs, applicationContext.theme)
            2 -> workoutsLayout.workoutColorLayout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.gradient_core, applicationContext.theme)
            3 -> workoutsLayout.workoutColorLayout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.gradient_yoga, applicationContext.theme)
            else -> workoutsLayout.workoutColorLayout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.gradient_default, applicationContext.theme)
        }

        binding.workoutsLayout.addView(workoutsLayout.root)

        workoutsLayout.workoutCard.setOnClickListener {
            val intent = Intent(this, WorkoutPlayActivity::class.java)
            intent.putExtra("workout", workout)
            startActivity(intent)
        }
    }

    private fun getWorkoutType(exercisesTypes: MutableList<Int>) : Int {

        var top = -1
        var score = 0

        // TODO: not working as intended
        for (type in exercisesTypes.indices) {
            if (exercisesTypes[type] > score) {
                top = type
                score = exercisesTypes[type]
            }
        }

        return if ((score + 1) / (exercisesTypes.sum() + 1) >= 0.4) top
        else ExerciseType.values().size
    }
}
