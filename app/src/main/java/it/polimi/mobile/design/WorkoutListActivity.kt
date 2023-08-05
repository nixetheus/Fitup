package it.polimi.mobile.design

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import it.polimi.mobile.design.databinding.ActivityWorkoutListBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutListBinding
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.enum.ExerciseType
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions


class WorkoutListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutListBinding
    private val helperDB = DatabaseHelper().getInstance()
    private var workoutArrayList = ArrayList<Workout>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityWorkoutListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createBindings()
        setupAnimations()
        retrieveWorkouts()

    }

    private fun createBindings() {

        binding.homeButton.setOnClickListener{
            val intent = Intent(this, CentralActivity::class.java)
            startActivity(intent)
        }

        binding.confirmAddWorkoutBtn.setOnClickListener{ createWorkout() }
        binding.searchWorkout.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean { return false }
            override fun onQueryTextChange(p0: String?): Boolean {
                showWorkouts(workoutArrayList.filter {
                        workout ->  workout.name!!.lowercase().contains(p0.toString().lowercase())
                        || ExerciseType.values()[HelperFunctions()
                    .getWorkoutType(workout.exercisesType!!)].toString().lowercase().contains(p0.toString().lowercase()) })
                return false
            }
        })

        binding.workoutNameField.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.action == KeyEvent.ACTION_DOWN) {
                binding.confirmAddWorkoutBtn.performClick()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun setupAnimations() {

        // Open
        binding.addWorkoutsButton.setOnClickListener{
            toggleAddWorkoutVisibility(true)
        }

        // Close
        binding.addWorkoutClose.setOnClickListener{
            toggleAddWorkoutVisibility(false)
        }

        // Close workout menu
        binding.closeWorkoutMenu.setOnClickListener{
            val animate = TranslateAnimation(0F, 0F, 0F, binding.editWorkoutLayout.height.toFloat())
            animate.duration = 500
            animate.fillAfter = true
            binding.editWorkoutLayout.startAnimation(animate)
        }
    }

    private fun toggleAddWorkoutVisibility(show: Boolean) {
        binding.addWorkoutCard.visibility = if (show) View.VISIBLE else View.GONE

        val translateY = if (!show) 0F else binding.addWorkoutCard.height.toFloat()
        val translateMinusY = if (show) 0F else binding.addWorkoutCard.height.toFloat() + 15.toPx()
        val animate = TranslateAnimation(0F, 0F, translateY, translateMinusY)
        animate.duration = 500
        animate.fillAfter = true
        binding.addWorkoutCard.startAnimation(animate)

        binding.addWorkoutsButton.isClickable = !show
        binding.addWorkoutClose.isClickable = show
    }

    private fun retrieveWorkouts() {
        helperDB.workoutsSchema.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutArrayList = helperDB.getWorkoutsFromSnapshot(snapshot)
                showWorkouts(workoutArrayList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }

    private fun createWorkout() {

        val name = binding.workoutNameField.text.toString()
        val uid  = helperDB.getFirebaseAuth().uid.toString()
        val wId  = helperDB.workoutsSchema.push().key!!

        if(name.isNotEmpty()) {
            val workout = Workout(wId, uid, name, "hip hop", 0, 0, 0.0f, 0f, 0, MutableList(4){0})
            helperDB.workoutsSchema.child(name).setValue(workout).addOnSuccessListener {
                Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
                toggleAddWorkoutVisibility(false)
            }
        }
        else Toast.makeText(this, "Fill in all fields to continue!!", Toast.LENGTH_SHORT).show()
    }

    fun showWorkouts(workouts: List<Workout>) {

        binding.workoutsListLayout.removeAllViews()
        for (workout in workouts) {

            val workoutsLayout = FragmentWorkoutListBinding.inflate(layoutInflater)

            with(workoutsLayout) {
                workoutDisplayNameList.text = workout.name!!.replaceFirstChar { it.uppercaseChar() }
                exercisesValueList.text = workout.numberOfExercises.toString()
                kcalValueList.text = workout.caloriesBurned.toString()
                bpmValueList.text = workout.averageBpmValue.toString()
                workoutsLayout.workoutColorLayout.background = HelperFunctions().getWorkoutColor(
                        (workout.exercisesType?.let { HelperFunctions().getWorkoutType(it) }!!), resources, applicationContext)
            }

            binding.workoutsListLayout.addView(workoutsLayout.root)

            workoutsLayout.workoutCardList.setOnClickListener {
                val intent = Intent(this, WorkoutPlayActivity::class.java)
                intent.putExtra("Workout", workout)
                startActivity(intent)
            }

            workoutsLayout.workoutCardList.setOnLongClickListener {
                renderModifyWorkout(workout, workoutsLayout)

                binding.modifyWorkoutButton.setOnClickListener{
                    onModifyWorkout(workout)
                }

                binding.deleteWorkoutButton.setOnClickListener{
                    onDeleteWorkout(workout)
                }
                true
            }
        }
    }

    private fun renderModifyWorkout(workout: Workout, workoutsLayout: FragmentWorkoutListBinding) {
        binding.editWorkoutLayout.visibility = View.VISIBLE
        binding.workoutMenuName.text = workout.name
        val animate = TranslateAnimation(0F, 0F, binding.addWorkoutCard.height.toFloat(), 0F)
        animate.duration = 500
        animate.fillAfter = true
        binding.editWorkoutLayout.startAnimation(animate)
        workoutsLayout.workoutCardList.isLongClickable = false
    }

    private fun onModifyWorkout(workout: Workout) {
        val intent = Intent(this, EditWorkoutActivity::class.java)
        intent.putExtra("Workout", workout)
        startActivity(intent)
    }

    private fun onDeleteWorkout(workout: Workout) {
        binding.editWorkoutLayout.visibility = View.GONE

        val workoutExercisesQuery = helperDB.workoutsExercisesSchema.orderByChild("workoutId").equalTo(workout.workoutId)
        val workoutsQuery = helperDB.workoutsSchema.orderByChild("workoutId").equalTo(workout.workoutId)

        val deleteTaskList = mutableListOf<Task<Void>>()

        if (workout.userId == helperDB.getFirebaseAuth().uid) {
            workoutExercisesQuery.get().addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach { exerciseSnap ->
                    deleteTaskList.add(exerciseSnap.ref.removeValue())
                }
            }

            workoutsQuery.get().addOnSuccessListener { dataSnapshot ->
                dataSnapshot.children.forEach { workoutSnapshot ->
                    deleteTaskList.add(workoutSnapshot.ref.removeValue())
                }
            }
        } else {
            Toast.makeText(this, "You can't delete this workout", Toast.LENGTH_SHORT).show()
        }
    }

    // TODO: modernize
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CentralActivity::class.java)
        startActivity(intent)
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}