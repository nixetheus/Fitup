package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.polimi.mobile.design.databinding.ActivityEditWorkoutBinding
import it.polimi.mobile.design.databinding.ExerciseInWorkoutBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.Workout
import it.polimi.mobile.design.entities.WorkoutExercise


class EditWorkoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditWorkoutBinding
    private lateinit var binding2:ExerciseInWorkoutBinding
    private lateinit var exerciseDatabase : DatabaseReference
    private lateinit var workoutExerciseDatabase: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var exerciseArrayList: ArrayList<Exercise>
    private lateinit var workoutExerciseList:ArrayList<WorkoutExercise>

    private lateinit var exercisesSpinner: Spinner
    private lateinit var adapter: ArrayAdapter<Exercise>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding2= ExerciseInWorkoutBinding.inflate(layoutInflater)


        var workout= intent.extras?.get("workout") as Workout
        workoutExerciseList=ArrayList<WorkoutExercise>()

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


        exerciseDatabase= FirebaseDatabase.getInstance().getReference("Exercise")
        exerciseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (workSnap in snapshot.children){
                        val exerciseData=workSnap.getValue(Exercise::class.java)
                        if (exerciseData!=null)
                            exerciseArrayList.add(exerciseData!!)
                    }
                    showExerciseSpinner(exerciseArrayList)

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
            exerciseDatabase= FirebaseDatabase.getInstance().getReference("WorkoutExercise")
            val id = exerciseDatabase.push().key!!
            val workoutId= workout.woId.toString()
            val exerciseId= binding.exercisesSpinner.selectedItem.toString()
            val sets=binding.setsInputValue.text.toString()
            val reps= binding.repsInputValue.text.toString()
            val rest=binding.restInputValue.text.toString()
            val workoutExercise= WorkoutExercise(id, workoutId, exerciseId, sets,reps,rest)
            if (sets.isNotEmpty()&&reps.isNotEmpty()&&rest.isNotEmpty()) {
                exerciseDatabase.child(id).setValue(workoutExercise).addOnSuccessListener {
                    Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
                    finish()
                    val intent = Intent(this, EditWorkoutActivity::class.java)
                    intent.putExtra("workout",workout /*as java.io.Serializable*/)
                    startActivity(intent)
                }
            }
            else Toast.makeText(this, "Fill in all fields to continue!!", Toast.LENGTH_SHORT).show()

        }
        workoutExerciseDatabase=FirebaseDatabase.getInstance().getReference("WorkoutExercise")
        workoutExerciseDatabase.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutExerciseList.clear()
                if (snapshot.exists()){
                    for (workSnap in snapshot.children){

                        val workData=workSnap.getValue(WorkoutExercise::class.java)
                        if (workData != null) {
                            if (workData.workoutId==workout.woId)
                                workoutExerciseList.add(workData!!)
                        }
                    }
                    showExerciseCards(workoutExerciseList)


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun showExerciseSpinner(exercises: List<Exercise>) {
        exercisesSpinner=binding.exercisesSpinner
        adapter= ArrayAdapter(this,android.R.layout.simple_spinner_item, exerciseArrayList)
        exercisesSpinner.adapter=adapter
        binding.caloriesDataBox

    }
    private fun showExerciseCards(workoutExercise: List<WorkoutExercise>){
        for (workoutExercise in workoutExercise) {
            binding2= ExerciseInWorkoutBinding.inflate(layoutInflater)
            binding2.exerciseNameExampleWorkout.text = workoutExercise.exerciseId.toString()
            binding2.repsValue.text = workoutExercise.reps.toString()
            binding2.setsValue.text = workoutExercise.sets.toString()
            binding2.restValue.text = workoutExercise.rest.toString()
            binding.workoutExercisesList.addView(binding2.root)

        }



    }
}

