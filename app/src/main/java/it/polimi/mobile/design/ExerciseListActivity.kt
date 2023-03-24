package it.polimi.mobile.design

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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
import it.polimi.mobile.design.databinding.FragmentFilterBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.enum.ExerciseType
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions

class ExerciseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExerciseListBinding
    private var exerciseDatabase = FirebaseDatabase.getInstance().getReference("Exercise")
    private val databaseHelperInstance = DatabaseHelper().getInstance()

    private var exerciseArrayList = ArrayList<Exercise>()
    private var filters = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityExerciseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBindings()

        exerciseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exerciseArrayList = databaseHelperInstance!!.getExercisesFromSnapshot(snapshot)
                showExercises(exerciseArrayList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })

        showFilters()

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
        if (filters.isEmpty()) showExercises(exerciseArrayList)
        else showExercises(exerciseArrayList.filter { filters.contains(it.type!!.ordinal)})
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

        binding.homeButton.setOnClickListener{
            val intent = Intent(this, CentralActivity::class.java)
            startActivity(intent)
        }

        binding.searchExercise.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                showExercises(exerciseArrayList.filter { exercise ->
                    exercise.name!!.lowercase().contains(p0!!.lowercase())
                            || exercise.type.toString().lowercase().contains(p0.lowercase())})
                return false
            }
        })
    }

    private fun createExercise(){

        val exerciseName = binding.exerciseNameField.text.toString()
        val kcalPerReps = HelperFunctions().parseFloatInput(binding.kcalInputValue.text.toString())
        val exType = exerciseTypeFromString(binding.typeOfEx.selectedItem.toString())
        val exp = HelperFunctions().parseFloatInput(binding.expInputValue.text.toString())

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
        for (exercise in exercises.sortedWith(compareBy({ it.type }, { it.name!!.length }))) {

            val exerciseLayout = FragmentExerciseListBinding.inflate(layoutInflater)
            exerciseLayout.exerciseNameList.text = exercise.name!!.replaceFirstChar { it.uppercaseChar() }
            exerciseLayout.caloriesRepsValue.text = exercise.caloriesPerRep!!.toString()
            exerciseLayout.experienceValue.text = exercise.experiencePerReps!!.toString()

            /*exerciseLayout.exercisesLayout.setBackgroundColor(
                HelperFunctions().getExerciseBackground(exercise.type!!, resources, applicationContext))
             */
            binding.exercisesListLayout.addView(exerciseLayout.root)
        }
    }
}