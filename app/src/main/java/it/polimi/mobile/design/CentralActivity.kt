package it.polimi.mobile.design


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.databinding.FragmentFilterBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutRecentBinding
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.entities.WorkoutUserData
import it.polimi.mobile.design.enum.WorkoutTypes
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions
import java.sql.Time
import java.time.Instant
import kotlin.math.log


class CentralActivity : AppCompatActivity() {

    private var filters = arrayListOf<Int>()
    private lateinit var binding: ActivityCentralBinding
    private val helperDB = DatabaseHelper().getInstance()
    private var firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userWorkouts: List<Workout>
    private lateinit var userWorkoutData: List<WorkoutUserData>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityCentralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserWorkoutData()
        createBindings()
        showUser()
        showFilters()
        workoutsCallback()
    }

    private fun createBindings() {

        binding.userImage.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.trophyLink.setOnClickListener{
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
        }

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

    private fun getUserWorkoutData() {
        helperDB.workoutUserDataSchema.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userWorkoutData = helperDB.getUserWorkoutDataFromSnapshot(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }

    private fun showUser(){
        firebaseAuth.uid?.let { userId ->
            helperDB.usersSchema.child(userId).get().addOnSuccessListener { userSnapshot ->
                val user = helperDB.getUserFromSnapshot(userSnapshot)
                binding.usernameText.text = user!!.username
                binding.userLevelValue.text = calculateLevel(user.exp!!).toString()
            }
        }
    }

    private fun calculateLevel(experience: Float): Int {
        // Constants for the logarithmic function
        val base = 100.0 // Base of the logarithm, adjust to control the gap between levels
        val scaleFactor = 1 // Scale factor to adjust the level range (0 to 100)

        // Calculate the level using a logarithmic function
        val level = (scaleFactor * (log(experience.toDouble(), base) + 1)).toInt()

        // Clamp the level to be between 0 and 100
        return level.coerceIn(0, 100)
    }

    @SuppressLint("Recycle")
    private fun showFilters() {

        for (typeFilter in WorkoutTypes.values()) {

            val filterLayout = FragmentFilterBinding.inflate(layoutInflater)
            filterLayout.filterDisplayName.text = typeFilter.name.replace("_", " ").lowercase().split(' ')
                .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
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
                showWorkouts()
            }
        }
    }

    private fun workoutsCallback() {
        helperDB.workoutsSchema.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userWorkouts = helperDB.getWorkoutsFromSnapshot(snapshot)
                showWorkouts()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }

    private fun showWorkouts() {

        // Eliminate workouts based on filters
        val workouts =
            if (filters.isNotEmpty())
                userWorkouts.filter { filters.contains(HelperFunctions().getWorkoutType(it.exercisesType!!))}
            else userWorkouts

        // Fill recent
        binding.workoutsLayoutRecent.removeAllViews()
        filterRecentWorkouts(workouts).map { workout -> showRecentWorkout(workout) }

        // Fill For You
        binding.workoutsLayout.removeAllViews()
        filterChosenWorkouts(workouts).map { workout -> showChosenWorkout(workout) }

        // Fill most popular
        binding.workoutsPopularLayout.removeAllViews()
        filterMostPopularWorkouts(workouts).map { workout -> showMostPopularWorkout(workout) }
    }

    private fun showRecentWorkout(workout: Workout) {

        val workoutsLayout = FragmentWorkoutRecentBinding.inflate(layoutInflater)
        workoutsLayout.workoutDisplayName.text = workout.name!!.replaceFirstChar { it.uppercaseChar() }
        workoutsLayout.colorTypeImage.setImageDrawable(getWorkoutColor(workout.exercisesType?.let { HelperFunctions().getWorkoutType(it) }!!))
        binding.workoutsLayoutRecent.addView(workoutsLayout.root)

        workoutsLayout.workoutCard.setOnClickListener {
            val intent = Intent(this, WorkoutPlayActivity::class.java)
            intent.putExtra("Workout", workout)
            startActivity(intent)
        }
    }

    private fun filterRecentWorkouts(workouts: List<Workout>) : List<Workout> {
        val workoutIdToLastDateMap = userWorkoutData.associateBy { it.workoutId }
        // Provide a default lastDate value when workoutId is not found in the map
        val defaultLastDate = Time.from(Instant.EPOCH).time
        return workouts.sortedBy { workout ->
            workoutIdToLastDateMap[workout.workoutId]?.lastDate ?: defaultLastDate }.reversed()
    }

    private fun showChosenWorkout(workout: Workout) {
        val chosenWorkoutLayout = fillWorkoutFragment(workout)
        binding.workoutsLayout.addView(chosenWorkoutLayout.root)
    }

    private fun filterChosenWorkouts(workouts: List<Workout>) : List<Workout> {
        val workoutIdToNumberPlayedMap = userWorkoutData.associateBy { it.workoutId }
        return workouts.sortedBy { workout ->
            workoutIdToNumberPlayedMap[workout.workoutId]?.numberOfTimesPlayed ?: 0
        }.reversed()
    }

    private fun filterMostPopularWorkouts(workouts: List<Workout>) : List<Workout> {
        return workouts.sortedBy {it.totalNumberOfTimesPlayed}.reversed()
    }

    private fun showMostPopularWorkout(workout: Workout) {
        val mostPopularWorkoutLayout = fillWorkoutFragment(workout)
        binding.workoutsPopularLayout.addView(mostPopularWorkoutLayout.root)
    }

    private fun fillWorkoutFragment(workout: Workout) : FragmentWorkoutBinding {

        val workoutLayout = FragmentWorkoutBinding.inflate(layoutInflater)

        with(workoutLayout) {
            workoutDisplayName.text = workout.name!!.replaceFirstChar { it.uppercaseChar() }
            exercisesValue.text = workout.numberOfExercises.toString()
            kcalValue.text = workout.caloriesBurned.toString()
            bpmValue.text = workout.averageBpmValue.toString()
            workoutColorLayout.background =
                getWorkoutColor(workout.exercisesType?.let { HelperFunctions().getWorkoutType(it) }!!)
        }

        workoutLayout.workoutCard.setOnClickListener {
            val intent = Intent(this, WorkoutPlayActivity::class.java)
            intent.putExtra("Workout", workout)
            startActivity(intent)
        }

        return workoutLayout
    }

    private fun getWorkoutColor(workoutType: Int) : Drawable {
        return HelperFunctions().getWorkoutColor(workoutType, resources, applicationContext)
    }

    fun getFiltersSize() : Int {
        return filters.size
    }
    override fun onBackPressed() {
    }
}
