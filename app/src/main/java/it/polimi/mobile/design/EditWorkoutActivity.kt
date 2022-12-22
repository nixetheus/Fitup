package it.polimi.mobile.design

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.polimi.mobile.design.databinding.ActivityEditWorkoutBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.entities.WorkoutExercise


class EditWorkoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditWorkoutBinding
    private lateinit var database : DatabaseReference
    private lateinit var toDelete: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var exerciseArrayList: ArrayList<Exercise>
    private lateinit var tempExerciseArrayList:ArrayList<Exercise>
    private lateinit var exercisesSpinner: Spinner
    private lateinit var adapter: ArrayAdapter<Exercise>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var workout= intent.extras?.get("workout") as Workout
        binding.workoutName.text=workout.name
        binding.openAddExerciseLayout.setOnClickListener{
            binding.addExerciseToWorkoutCard.visibility=View.VISIBLE
            val animate = TranslateAnimation(
                0F,  // fromXDelta
                0F,  // toXDelta
                binding.addExerciseToWorkoutCard.height.toFloat(),// fromYDelta
                0F
            )

            animate.duration = 500
            animate.fillAfter = true
            binding.addExerciseToWorkoutCard.startAnimation(animate)
        }
        exerciseArrayList = arrayListOf<Exercise>()

        tempExerciseArrayList = arrayListOf<Exercise>()
        database= FirebaseDatabase.getInstance().getReference("Exercise")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (workSnap in snapshot.children){
                        val exerciseData=workSnap.getValue(Exercise::class.java)
                        if (exerciseData!=null)
                            exerciseArrayList.add(exerciseData!!)
                    }
                    showExercise(exerciseArrayList)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.closeModifyExercise.setOnClickListener{
            binding.addExerciseToWorkoutCard.visibility=View.GONE
            val animate = TranslateAnimation(
                0F,  // fromXDelta
                0F,  // toXDelta
                0F,// fromYDelta
                binding.addExerciseToWorkoutCard.height.toFloat()
            )

            animate.duration = 500
            animate.fillAfter = true
            binding.addExerciseToWorkoutCard.startAnimation(animate)

        }
        binding.confirmAddWorkoutBtn.setOnClickListener{

            val ex= binding.exercisesSpinner.selectedItem
            database= FirebaseDatabase.getInstance().getReference("WorkoutExercise")
            val id = database.push().key!!
            val workoutId= workout.woId.toString()
            val exerciseId= binding.exercisesSpinner.selectedItem.toString()
            val sets=binding.setsInputValue.text.toString()
            val reps= binding.repsInputValue.text.toString()
            val rest=binding.restInputValue.text.toString()
            val workoutExercise= WorkoutExercise(id, workoutId, exerciseId, sets,reps,rest)
            if (sets.isNotEmpty()&&reps.isNotEmpty()&&rest.isNotEmpty()) {
                database.child(id).setValue(workoutExercise).addOnSuccessListener {
                    Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
                    finish()
                    val intent = Intent(this, EditWorkoutActivity::class.java)
                    intent.putExtra("workout",workout /*as java.io.Serializable*/)
                    startActivity(intent)
                }
            }
            else Toast.makeText(this, "Fill in all fields to continue!!", Toast.LENGTH_SHORT).show()

        }

    }
    private fun showExercise(exercises: List<Exercise>) {
        exercisesSpinner=binding.exercisesSpinner
        adapter= ArrayAdapter(this,android.R.layout.simple_spinner_item, exerciseArrayList)
        exercisesSpinner.adapter=adapter
        binding.caloriesDataBox

    }

}