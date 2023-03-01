package it.polimi.mobile.design


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutBinding
import it.polimi.mobile.design.entities.Workout
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

            val workoutsLayout = FragmentWorkoutBinding.inflate(layoutInflater)

            workoutsLayout.workoutDisplayName.text = workout.name

            // TODO: real values
            workoutsLayout.exercisesValue.text = getString(R.string.null_value)
            workoutsLayout.kcalValue.text = getString(R.string.null_value)
            workoutsLayout.bpmValue.text = getString(R.string.null_value)

            workoutsLayout.exercisesLabel.text = getString(R.string.number_exercises_label)
            workoutsLayout.kcalLabel.text = getString(R.string.calories_data_label)
            workoutsLayout.bpmLabel.text = getString(R.string.bpm_data_label)

            // TODO: real color
            /*val test = listOf(R.drawable.arms_background, R.drawable.core_background, R.drawable.legs_background,
                R.drawable.mind_background)
            workoutsLayout.workoutLayout.background = theme.getDrawable(test.random())*/

            binding.workoutsLayout.addView(workoutsLayout.root)

            workoutsLayout.workoutCard.setOnClickListener {
                val intent = Intent(this, WorkoutPlayActivity::class.java)
                intent.putExtra("workout", workout)
                startActivity(intent)
            }
        }
    }
}
