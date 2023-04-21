package it.polimi.mobile.design


import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.databinding.FragmentFilterBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutRecentBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.User
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.entities.WorkoutExercise
import it.polimi.mobile.design.enum.ExerciseType
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions
import kotlin.properties.Delegates


class CentralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCentralBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private val databaseInstance = FirebaseDatabase.getInstance()
    private var exp:Float = 0.0f

    private var exerciseDatabase = FirebaseDatabase.getInstance().getReference("Exercise")
    private var workoutExerciseDatabase = FirebaseDatabase.getInstance().getReference("WorkoutExercise")
    private val databaseHelperInstance = DatabaseHelper().getInstance()

    private var exerciseArrayList = ArrayList<Exercise>()
    private var workoutExerciseList = ArrayList<WorkoutExercise>()

    private var filters = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityCentralBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configDatabases()
        showUser()
        createBindings()
        workoutsCallback()
        showFilters()
    }

    private fun workoutsCallback() {
        val workoutsSchema = databaseInstance.getReference("Workout")
        workoutsSchema.orderByChild("ranking").limitToFirst(6).addValueEventListener(object : ValueEventListener {
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
                binding.userLevelValue.text = (user.exp!! / 10).toInt().toString()
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

        binding.userImage.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            val usersSchema = databaseInstance.getReference("Users")
            firebaseAuth.uid?.let { userId ->
                usersSchema.child(userId).get().addOnSuccessListener { userSnapshot ->
                    val user = databaseHelperInstance!!.getUserFromSnapshot(userSnapshot)
                    if (user != null) {
                        intent.putExtra("username", user.username)
                    }
                }
            }
            startActivity(intent)
        }

        binding.trophyLink.setOnClickListener{
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("Recycle")
    private fun showFilters() {

        for (typeFilter in ExerciseType.values()) {

            val filterLayout = FragmentFilterBinding.inflate(layoutInflater)
            filterLayout.filterDisplayName.text = typeFilter.name.lowercase().replaceFirstChar { it.uppercaseChar() }
            binding.filtersLayout.addView(filterLayout.root)


            val attrs = intArrayOf(R.attr.colorSecondary)
            val ta = obtainStyledAttributes(R.style.Theme_MobileProject, attrs)

            filterLayout.filterCard.setOnClickListener {
                if (typeFilter.ordinal !in filters) {
                    filters.add(typeFilter.ordinal)
                    filterLayout.filterDisplayName.setBackgroundColor(resources.getColor(R.color.lilla, applicationContext.theme))
                } else {
                    filters.remove(typeFilter.ordinal)
                    filterLayout.filterDisplayName.setBackgroundColor(ta.getColor(0, Color.BLACK))
                }
                applyFilter()
            }
        }
    }

    private fun applyFilter() {
        val workoutsSchema = databaseInstance.getReference("Workout")
        workoutsSchema.orderByChild("ranking").limitToFirst(5).get().addOnSuccessListener { graphsSnapshot ->
            val workouts = DatabaseHelper().getWorkoutsFromSnapshot(graphsSnapshot)
            if (filters.isEmpty()) showWorkouts(workouts)
            else showWorkouts(workouts.filter {
                    filters.contains(HelperFunctions().getWorkoutType(it.exercisesType!!))})
        }
    }

    private fun showWorkouts(workouts: List<Workout>) {
        binding.workoutsLayout.removeAllViews()
        binding.workoutsLayoutRecent.removeAllViews()
        for (workout in workouts) {
            showRecentWorkout(workout)
            showChosenWorkout(workout)
        }
    }

    private fun showRecentWorkout(workout: Workout) {


        val workoutsLayout = FragmentWorkoutRecentBinding.inflate(layoutInflater)

        workoutsLayout.workoutDisplayName.text = workout.name!!.replaceFirstChar { it.uppercaseChar() }
        var exp = 0f
        var kcalTot = 0F
        for(workoutExercise in workoutExerciseList.filter { we -> we.workoutId == workout.workoutId }) {
            for (exercise in exerciseArrayList.filter { ex -> ex.eid == workoutExercise.exerciseId})
            {
                exp += (workoutExercise.reps!! * workoutExercise.sets!!) * exercise.experiencePerReps!!
                kcalTot += (workoutExercise.reps * exercise.caloriesPerRep!!) * workoutExercise.sets
            }
        }

        when(workout.exercisesType?.let { HelperFunctions().getWorkoutType(it) }) {
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
            intent.putExtra("exp", exp)

            startActivity(intent)
        }
    }

    private fun showChosenWorkout(workout: Workout) {


        val workoutsLayout = FragmentWorkoutBinding.inflate(layoutInflater)

        workoutsLayout.workoutDisplayName.text = workout.name!!.replaceFirstChar { it.uppercaseChar() }
        var exp=0F
        var kcalTot = 0F
        for(workoutExercise in workoutExerciseList.filter { we -> we.workoutId == workout.workoutId }) {
            for (exercise in exerciseArrayList.filter { ex -> ex.eid == workoutExercise.exerciseId})
            {
                exp += (workoutExercise.reps!! * workoutExercise.sets!!) * exercise.experiencePerReps!!
                kcalTot += (workoutExercise.reps * exercise.caloriesPerRep!!) * workoutExercise.sets
            }
        }

        // TODO: real values
        workoutsLayout.exercisesValue.text = exp.toString()
        workoutsLayout.kcalValue.text = kcalTot.toString()
        workoutsLayout.bpmValue.text = getString(R.string.null_value)

        workoutsLayout.exercisesLabel.text = getString(R.string.number_exercises_label)
        workoutsLayout.kcalLabel.text = getString(R.string.calories_data_label)
        workoutsLayout.bpmLabel.text = getString(R.string.bpm_data_label)

        when(workout.exercisesType?.let { HelperFunctions().getWorkoutType(it) }) {
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
            intent.putExtra("exp", exp)
            startActivity(intent)
        }
    }
    private fun configDatabases() {

        exerciseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exerciseArrayList = DatabaseHelper().getExercisesFromSnapshot(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })

        workoutExerciseDatabase.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutExerciseList = DatabaseHelper().getAllWorkoutsExercisesFromSnapshot(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }


}
