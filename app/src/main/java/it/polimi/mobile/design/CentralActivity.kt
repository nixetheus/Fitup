package it.polimi.mobile.design


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.polimi.mobile.design.databinding.ActivityCentralBinding
import it.polimi.mobile.design.databinding.FragmentFilterBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutRecentBinding
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.enum.ExerciseType
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions


class CentralActivity : AppCompatActivity() {

    private var filters = arrayListOf<Int>()
    private lateinit var binding: ActivityCentralBinding
    private val helperDB = DatabaseHelper().getInstance()
    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityCentralBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun showUser(){
        firebaseAuth.uid?.let { userId ->
            helperDB.usersSchema.child(userId).get().addOnSuccessListener { userSnapshot ->
                val user = helperDB.getUserFromSnapshot(userSnapshot)
                binding.usernameText.text = user!!.username
                binding.userLevelValue.text = (user.exp!! / 10).toInt().toString()
            }
        }
    }

    @SuppressLint("Recycle")
    private fun showFilters() {

        for (typeFilter in ExerciseType.values()) {

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
            }
        }
    }

    private fun workoutsCallback() {
        helperDB.workoutsSchema.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showWorkouts(helperDB.getWorkoutsFromSnapshot(snapshot))
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }

    private fun showWorkouts(workouts: List<Workout>) {

        // Eliminate workouts based on filters
        workouts.filter { filters.contains(HelperFunctions().getWorkoutType(it.exercisesType!!))}

        // Fill recent
        binding.workoutsLayoutRecent.removeAllViews()
        filterRecentWorkouts(workouts).map { workout -> showRecentWorkout(workout) }

        // Fill For You
        binding.workoutsLayout.removeAllViews()
        workouts.map { workout -> showChosenWorkout(workout) }

        // Fill most popular
        binding.workoutsPopularLayout.removeAllViews()
        filterMostPopularWorkouts(workouts).map { workout -> showMostPopularWorkout(workout) }
    }

    private fun showRecentWorkout(workout: Workout) {

        val workoutsLayout = FragmentWorkoutRecentBinding.inflate(layoutInflater)
        workoutsLayout.workoutDisplayName.text = workout.name!!.replaceFirstChar { it.uppercaseChar() }
        workoutsLayout.colorTypeImage.setImageDrawable(getWorkoutColor(workout.exercisesType?.let { HelperFunctions().getWorkoutType(it) }!!))
        binding.workoutsLayoutRecent.addView(workoutsLayout.root)

        // TODO change intent extras
        workoutsLayout.workoutCard.setOnClickListener {
            val intent = Intent(this, WorkoutPlayActivity::class.java)
            intent.putExtra("workout", workout)
            intent.putExtra("exp", 0)
            startActivity(intent)
        }
    }

    private fun filterRecentWorkouts(workouts: List<Workout>) : List<Workout> {
        // TODO: Logic
        return workouts
    }

    private fun showChosenWorkout(workout: Workout) {

        val chosenWorkoutLayout = fillWorkoutFragment(workout)
        binding.workoutsLayout.addView(chosenWorkoutLayout.root)
    }

    private fun filterMostPopularWorkouts(workouts: List<Workout>) : List<Workout> {
        // TODO: Logic
        return workouts
    }

    private fun showMostPopularWorkout(workout: Workout) {
        val mostPopularWorkoutLayout = fillWorkoutFragment(workout)
        binding.workoutsPopularLayout.addView(mostPopularWorkoutLayout.root)
    }

    private fun fillWorkoutFragment(workout: Workout) : FragmentWorkoutBinding {

        val workoutLayout = FragmentWorkoutBinding.inflate(layoutInflater)

        with(workoutLayout) {
            workoutDisplayName.text = workout.name!!.replaceFirstChar { it.uppercaseChar() }
            exercisesValue.text = "TODO"
            kcalValue.text = "TODO"
            bpmValue.text = getString(R.string.null_value)
            workoutColorLayout.background =
                getWorkoutColor(workout.exercisesType?.let { HelperFunctions().getWorkoutType(it) }!!)
        }

        // TODO change intent extras
        workoutLayout.workoutCard.setOnClickListener {
            val intent = Intent(this, WorkoutPlayActivity::class.java)
            intent.putExtra("workout", workout)
            intent.putExtra("exp", 0)
            startActivity(intent)
        }

        return workoutLayout
    }

    private fun getWorkoutColor(workoutType: Int) : Drawable {
        return when(workoutType) {
            0    -> ResourcesCompat.getDrawable(resources, R.drawable.gradient_arms, applicationContext.theme)!!
            1    -> ResourcesCompat.getDrawable(resources, R.drawable.gradient_legs, applicationContext.theme)!!
            2    -> ResourcesCompat.getDrawable(resources, R.drawable.gradient_core, applicationContext.theme)!!
            3    -> ResourcesCompat.getDrawable(resources, R.drawable.gradient_yoga, applicationContext.theme)!!
            else -> ResourcesCompat.getDrawable(resources, R.drawable.gradient_default, applicationContext.theme)!!
        }
    }

    fun getFiltersSize() : Int {
        return filters.size
    }
}
