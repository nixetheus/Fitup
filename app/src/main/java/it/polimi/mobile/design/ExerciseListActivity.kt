package it.polimi.mobile.design

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.polimi.mobile.design.databinding.ActivityExerciseListBinding
import it.polimi.mobile.design.databinding.FragmentExerciseListBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.enum.ExerciseType
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions

class ExerciseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExerciseListBinding
    private var exerciseDatabase = FirebaseDatabase.getInstance().getReference("Exercise")
    private val databaseHelperInstance = DatabaseHelper().getInstance()

    private var exerciseArrayList = ArrayList<Exercise>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityExerciseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBindings()

        exerciseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showExercises(databaseHelperInstance!!.getExercisesFromSnapshot(snapshot))
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })

    }

    private fun setBindings() {

        binding.confirmAddExerciseBtn.setOnClickListener{
            createExercise()
            binding.addExerciseCard.visibility = View.GONE
        }
        binding.addExerciseButton.setOnClickListener{
            binding.addExerciseCard.visibility = View.VISIBLE
        }
        binding.addExerciseClose.setOnClickListener{
            binding.addExerciseCard.visibility = View.GONE
        }

        binding.searchExercise.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                showExercises(exerciseArrayList.filter { exercise ->
                    exercise.name!!.contains(p0!!) || exercise.type.toString().contains(p0)
                })
                return false
            }
        })
    }

    private fun createExercise(){

        val exerciseName = binding.exerciseNameField.text.toString()
        val kcalPerReps = HelperFunctions().parseFloatInput(binding.kcalInputValue.text.toString())
        val exType = exerciseTypeFromString(binding.typeOfEx.selectedItem.toString())
        val exp = HelperFunctions().parseIntInput(binding.expInputValue.text.toString())

        val exerciseId = exerciseDatabase.push().key!!
        if (exerciseName.isNotEmpty()){
            val exercise = Exercise(exerciseId, exerciseName, kcalPerReps, exType, exp)
            exerciseDatabase.child(exerciseId).setValue(exercise).addOnSuccessListener {
                Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ExerciseListActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        else
            Toast.makeText(this, "Fill in all fields to continue", Toast.LENGTH_SHORT).show()
    }

    private fun exerciseTypeFromString(typeString : String) : ExerciseType {
        return when(typeString) {
            "Arms" -> ExerciseType.ARMS
            "Legs" -> ExerciseType.LEGS
            "Chest" -> ExerciseType.CHEST
            else -> ExerciseType.ABDOMEN
        }
    }

    private fun showExercises(exercises: List<Exercise>) {

        binding.exercisesListLayout.removeAllViews()
        for (exercise in exercises) {

            val exerciseLayout = FragmentExerciseListBinding.inflate(layoutInflater)
            exerciseLayout.exerciseNameList.text = exercise.name
            binding.exercisesListLayout.addView(exerciseLayout.root)
        }
    }
}