package it.polimi.mobile.design

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.polimi.mobile.design.databinding.ActivityEditWorkoutBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.entities.Workout


class EditWorkoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditWorkoutBinding
    private lateinit var database : DatabaseReference
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

    }
    private fun showExercise(exercises: List<Exercise>) {
        exercisesSpinner=binding.exercisesSpinner
        adapter= ArrayAdapter(this,android.R.layout.simple_spinner_item, exerciseArrayList)
        exercisesSpinner.adapter=adapter

    }

}