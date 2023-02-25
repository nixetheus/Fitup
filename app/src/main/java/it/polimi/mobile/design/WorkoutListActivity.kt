package it.polimi.mobile.design

import android.widget.*
import android.util.Log
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.content.ContentValues
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth
import it.polimi.mobile.design.enum.WorkoutType
import it.polimi.mobile.design.entities.Workout
import androidx.appcompat.app.AppCompatActivity
import it.polimi.mobile.design.entities.Exercise
import android.view.animation.TranslateAnimation
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.entities.WorkoutExercise
import it.polimi.mobile.design.databinding.FragmentWorkoutListBinding
import it.polimi.mobile.design.databinding.ActivityWorkoutListBinding


class WorkoutListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutListBinding
    private var database = FirebaseDatabase.getInstance()
    private var databaseWorkout = database.getReference("Workout")
    private var databaseExercise = database.getReference("Exercise")
    private var databaseWorkoutExercise = database.getReference("WorkoutExercise")
    private val databaseHelperInstance = DatabaseHelper().getInstance()

    private var firebaseAuth = FirebaseAuth.getInstance()


    private var workoutArrayList = ArrayList<Workout>()
    private var exerciseArrayList = ArrayList<Exercise>()
    private var workoutExerciseList = ArrayList<WorkoutExercise>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityWorkoutListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // TODO: Can remove? showWorkouts(workoutArrayList)

        setupUI()
        configDatabases()
        setupAnimations()
        binding.confirmAddWorkoutBtn.setOnClickListener{ createWorkout() }
        binding.searchWorkout.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean { return false }
            override fun onQueryTextChange(p0: String?): Boolean {
                showWorkouts(workoutArrayList.filter {
                    workout ->  workout.name!!.contains(p0.toString())
                        || workout.type.toString().contains(p0.toString()) })
                return false
            }
        })
    }

    private fun configDatabases() {

        databaseExercise.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exerciseArrayList = DatabaseHelper().getExercisesFromSnapshot(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })

        databaseWorkoutExercise.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutExerciseList = DatabaseHelper().getAllWorkoutsExercisesFromSnapshot(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }

    private fun setupUI() {
        databaseWorkout.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                showWorkouts(databaseHelperInstance!!.getWorkoutsFromSnapshot(snapshot))
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }

    private fun setupAnimations() {

        // Open
        binding.addWorkoutsButton.setOnClickListener{
            binding.addWorkoutCard.visibility = View.VISIBLE
            val animate = TranslateAnimation(0F, 0F, binding.addWorkoutCard.height.toFloat(), 0F)

            animate.duration = 500
            animate.fillAfter = true
            binding.addWorkoutCard.startAnimation(animate)
            binding.addWorkoutsButton.isClickable=false
            binding.addWorkoutClose.isClickable=true
        }

        // Close
        binding.addWorkoutClose.setOnClickListener{
            val animate = TranslateAnimation(0F, 0F, 0F, binding.addWorkoutCard.height.toFloat())
            animate.duration = 500
            animate.fillAfter = true
            binding.addWorkoutCard.startAnimation(animate)
            binding.addWorkoutsButton.isClickable=true
            binding.addWorkoutClose.isClickable=false
        }

        // Close workout menu
        binding.closeWorkoutMenu.setOnClickListener{
            val animate = TranslateAnimation(0F, 0F, 0F, binding.editWorkoutLayout.height.toFloat())
            animate.duration = 500
            animate.fillAfter = true
            binding.editWorkoutLayout.startAnimation(animate)
            //workoutsLayout.workoutCard.isLongClickable = true
        }
    }

    private fun createWorkout(){
        val name=binding.workoutNameField.text.toString()
        val uid=firebaseAuth.uid.toString()

        val wId = databaseWorkout.push().key!!
        if(name.isNotEmpty()) {
            val workout = Workout(wId, uid, name, WorkoutType.RELAX, "hip hop")
            databaseWorkout.child(name).setValue(workout).addOnSuccessListener {
                Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, WorkoutListActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        else Toast.makeText(this, "Fill in all fields to continue!!", Toast.LENGTH_SHORT).show()

    }

    private fun showWorkouts(workouts: List<Workout>) {


        binding.workoutsListLayout.removeAllViews()

        for (workout in workouts) {

            val workoutsLayout = FragmentWorkoutListBinding.inflate(layoutInflater)

            workoutsLayout.workoutDisplayName.text = workout.name

            var exp = 0f
            var kcalTot = 0F
            for(workoutExercise in workoutExerciseList.filter { we -> we.workoutId == workout.workoutId }) {
                for (exercise in exerciseArrayList.filter { ex -> ex.eid == workoutExercise.exerciseId})
                {
                    exp += (workoutExercise.reps!! * workoutExercise.sets!!) * exercise.experiencePerReps!!
                    kcalTot += (workoutExercise.reps * exercise.caloriesPerRep!!) * workoutExercise.sets
                }
            }

            // TODO: real values
            workoutsLayout.exercisesValue.text = getString(R.string.null_value)
            workoutsLayout.kcalValue.text = kcalTot.toString()
            workoutsLayout.bpmValue.text = getString(R.string.null_value)

            workoutsLayout.exercisesLabel.text = getString(R.string.number_exercises_label)
            workoutsLayout.kcalLabel.text = getString(R.string.calories_data_label)
            workoutsLayout.bpmLabel.text = getString(R.string.bpm_data_label)

            // TODO: real color
            val test = listOf(R.drawable.arms_background, R.drawable.core_background, R.drawable.legs_background,
                R.drawable.mind_background)
            workoutsLayout.workoutLayout.background = theme.getDrawable(test.random())

            binding.workoutsListLayout.addView(workoutsLayout.root)

            workoutsLayout.workoutCard.setOnClickListener {
                val intent = Intent(this, WorkoutPlayActivity::class.java)
                intent.putExtra("workout", workout)
                startActivity(intent)
            }

            workoutsLayout.workoutCard.setOnLongClickListener {

                binding.editWorkoutLayout.visibility = View.VISIBLE
                binding.workoutMenuName.text = workout.name
                val animate = TranslateAnimation(0F, 0F, binding.addWorkoutCard.height.toFloat(), 0F)
                animate.duration = 500
                animate.fillAfter = true
                binding.editWorkoutLayout.startAnimation(animate)
                workoutsLayout.workoutCard.isLongClickable = false

                binding.modifyWorkoutButton.setOnClickListener{
                    onModifyWorkout(workout, workoutsLayout)
                }

                binding.deleteWorkoutButton.setOnClickListener{
                    onDeleteWorkout(workout)
                }
                true
            }
        }
    }

    private fun onModifyWorkout(workout: Workout, workoutsLayout: FragmentWorkoutListBinding) {
        val intent = Intent(this, EditWorkoutActivity::class.java)
        intent.putExtra("workout", workout)
        startActivity(intent)
        workoutsLayout.workoutCard.isLongClickable = true
        binding.editWorkoutLayout.visibility = View.GONE
    }

    private fun onDeleteWorkout(workout: Workout) {

        binding.editWorkoutLayout.visibility = View.GONE

        val workoutExercise = database.reference.child("WorkoutExercise").
        orderByChild("workoutId").equalTo(workout.workoutId)

        workoutExercise.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (exerciseSnap in dataSnapshot.children) {
                    exerciseSnap.ref.removeValue()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
            }
        })


        val workoutToRemove = database.reference.child("Workout").
        orderByChild("workoutId").equalTo(workout.workoutId)

        workoutToRemove.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (workoutSnapshot in dataSnapshot.children) {
                    workoutSnapshot.ref.removeValue()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
            }
        })

        val intent = Intent(this, WorkoutListActivity::class.java)
        startActivity(intent)
    }

}