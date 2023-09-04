package it.polimi.mobile.design

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import it.polimi.mobile.design.databinding.ActivityExerciseListBinding
import it.polimi.mobile.design.databinding.FragmentExerciseListBinding
import it.polimi.mobile.design.databinding.FragmentFilterBinding
import it.polimi.mobile.design.entities.Exercise
import it.polimi.mobile.design.enum.ExerciseType
import it.polimi.mobile.design.enum.WorkoutTypes
import it.polimi.mobile.design.helpers.DatabaseHelper
import it.polimi.mobile.design.helpers.HelperFunctions

class ExerciseListActivity : AppCompatActivity() {

    private val helperDB = DatabaseHelper().getInstance()
    private lateinit var binding: ActivityExerciseListBinding

    private var filters = arrayListOf<Int>()
    private var exerciseArrayList = ArrayList<Exercise>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityExerciseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBindings()
        showFilters()
        retrieveExercises()
    }

    private fun setBindings() {

        binding.homeButton.setOnClickListener{
            val intent = Intent(this, CentralActivity::class.java)
            startActivity(intent)
        }

        binding.addExerciseButton.setOnClickListener{
            showAddExercise()
        }

        binding.addExerciseClose.setOnClickListener{
            hideAddExercise()
        }

        binding.confirmAddExerciseBtn.setOnClickListener {
            saveExercise()
            hideAddExercise()
        }

        binding.closeExerciseMenu.setOnClickListener {
            binding.exerciseMenuCard.visibility = View.GONE
        }

        binding.searchExercise.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?) = false

            override fun onQueryTextChange(p0: String?): Boolean {
                showExercises(exerciseArrayList.filter { exercise ->
                    exercise.name?.contains(p0.orEmpty(), ignoreCase = true) == true ||
                            exercise.type.toString().contains(p0.orEmpty(), ignoreCase = true)
                })
                return false
            }
        })
    }

    private fun showAddExercise() {
        binding.addExerciseCard.visibility = View.VISIBLE
        val openAddExerciseMenuAnimation = TranslateAnimation(
            0F, 0F, binding.addExerciseCard.height.toFloat(), 0F)
        openAddExerciseMenuAnimation.duration = 500
        openAddExerciseMenuAnimation.fillAfter = true
        binding.addExerciseCard.startAnimation(openAddExerciseMenuAnimation)
        binding.addExerciseCard.clearAnimation()
    }

    private fun hideAddExercise() {
        binding.addExerciseCard.visibility = View.GONE
        val closeAddExerciseMenuAnimation = TranslateAnimation(
            0F, 0F, 0F, binding.addExerciseCard.height.toFloat() + 5.toPx())
        closeAddExerciseMenuAnimation.duration = 500
        closeAddExerciseMenuAnimation.fillAfter = true
        binding.addExerciseCard.startAnimation(closeAddExerciseMenuAnimation)
        binding.addExerciseCard.clearAnimation()
    }

    @SuppressLint("Recycle")
    private fun showFilters() {

        for (typeFilter in WorkoutTypes.values()) {

            val filterLayout = FragmentFilterBinding.inflate(layoutInflater).apply {
                filterDisplayName.text = typeFilter.name.replace("_", " ").lowercase()
                    .split(' ').joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
            }

            val attrs = intArrayOf(R.attr.colorSecondary)
            val ta = obtainStyledAttributes(R.style.Theme_MobileProject, attrs)

            filterLayout.filterCard.setOnClickListener {
                filters.runCatching {
                    if (typeFilter.ordinal !in this) {
                        add(typeFilter.ordinal)
                        filterLayout.filterDisplayName.setBackgroundColor(resources.getColor(R.color.lilla, applicationContext.theme))
                    } else {
                        remove(typeFilter.ordinal)
                        filterLayout.filterDisplayName.setBackgroundColor(ta.getColor(0, Color.BLACK))
                    }
                    applyFilter()
                }
            }

            binding.filtersLayout.addView(filterLayout.root)
        }
    }

    private fun applyFilter() {
        val ranges = mutableListOf<IntRange>()
        for (filter in filters) {
            when(filter) {
                WorkoutTypes.UPPER_BODY.ordinal
                -> ranges.add(ExerciseType.CHEST.ordinal..ExerciseType.TRICEPS.ordinal)
                WorkoutTypes.ABDOMINALS.ordinal
                -> ranges.add(ExerciseType.ABDOMINALS.ordinal..ExerciseType.OBLIQUES.ordinal )
                WorkoutTypes.LOWER_BODY.ordinal
                -> ranges.add(ExerciseType.QUADRICEPS.ordinal..ExerciseType.CALVES.ordinal)
                WorkoutTypes.YOGA.ordinal
                -> ranges.add(ExerciseType.YOGA.ordinal..ExerciseType.YOGA.ordinal)
                WorkoutTypes.CARDIO.ordinal
                -> ranges.add(ExerciseType.RUNNING.ordinal..ExerciseType.KICKBOXING.ordinal)
            }
        }

        if (filters.isEmpty()) showExercises(exerciseArrayList)
        else showExercises(exerciseArrayList.filter { exercise ->
            val typeOrdinal = exercise.type?.ordinal
            typeOrdinal != null && ranges.any { typeOrdinal in it }
        })
    }

    private fun retrieveExercises() {
        helperDB.exercisesSchema.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exerciseArrayList = helperDB.getExercisesFromSnapshot(snapshot)
                showExercises(exerciseArrayList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Couldn't retrieve data...")
            }
        })
    }

    private fun showExercises(exercises: List<Exercise>) {

        var index = 0
        binding.exercisesListLayout.removeAllViews()
        for (exercise in exercises.sortedWith(compareBy({ it.type }, { it.name!!.length }))) {

            val exerciseLayout = FragmentExerciseListBinding.inflate(layoutInflater)
            with(exerciseLayout) {
                exerciseCard.tag = index++
                exerciseNameList.text  = exercise.name!!.replaceFirstChar { it.uppercaseChar() }
                caloriesRepsValue.text = exercise.caloriesPerRep!!.toString()
                experienceValue.text   = exercise.experiencePerReps!!.toString()
                typeValue.text = exercise.type!!.toString()

                exerciseCard.setCardBackgroundColor(HelperFunctions().getExerciseBackground(exercise.type!!, resources, applicationContext))
                setDeleteMenuAnimation(exerciseCard, exercise)
            }
            binding.exercisesListLayout.addView(exerciseLayout.root)
        }
    }

    private fun setDeleteMenuAnimation(exerciseCard: View, exercise: Exercise) {
        exerciseCard.setOnLongClickListener {
            binding.exerciseMenuCard.visibility = View.VISIBLE
            binding.exerciseMenuName.text = exercise.name!!.uppercase()
            val animate = TranslateAnimation(0F, 0F, binding.addExerciseCard.height.toFloat(), 0F)
            animate.duration = 500
            animate.fillAfter = true
            binding.deleteExerciseLayout.startAnimation(animate)
            binding.deleteExerciseButton.setOnClickListener {
                onDeleteExercise(exercise)
            }
            false
        }
    }

    private fun saveExercise() {

        val exerciseId = helperDB.exercisesSchema.push().key!!
        if (binding.exerciseNameField.text.toString().isNotEmpty()) {
            val exercise = createExercise(exerciseId)
            helperDB.exercisesSchema.child(exerciseId).setValue(exercise).addOnSuccessListener {
                Toast.makeText(this, "Successfully saved!!", Toast.LENGTH_SHORT).show()
            }
        }
        else
            Toast.makeText(this, "Fill in all fields to continue", Toast.LENGTH_SHORT).show()
    }

    private fun createExercise(exerciseId: String) : Exercise {

        val exerciseName = binding.exerciseNameField.text.toString()
        val kcalPerReps  = HelperFunctions().parseFloatInput(binding.kcalInputValue.text.toString())
        val exType       = exerciseTypeFromString(binding.typeOfEx.selectedItem.toString())
        val exp          = HelperFunctions().parseFloatInput(binding.expInputValue.text.toString())

        return Exercise(helperDB.getFirebaseAuth().uid, exerciseId, exerciseName, kcalPerReps, exType, exp)
    }

    private fun exerciseTypeFromString(typeString : String) : ExerciseType {
        return when(typeString) {
            "CHEST" -> ExerciseType.CHEST
            "BACK" -> ExerciseType.BACK
            "SHOULDERS" -> ExerciseType.SHOULDERS
            "BICEPS" -> ExerciseType.BICEPS
            "TRICEPS" -> ExerciseType.TRICEPS
            "ABDOMINALS" -> ExerciseType.ABDOMINALS
            "OBLIQUES" -> ExerciseType.OBLIQUES
            "QUADRICEPS" -> ExerciseType.QUADRICEPS
            "HAMSTRINGS" -> ExerciseType.HAMSTRINGS
            "GLUTES" -> ExerciseType.GLUTES
            "CALVES" -> ExerciseType.CALVES
            "YOGA" -> ExerciseType.YOGA
            "RUNNING" -> ExerciseType.RUNNING
            "CYCLING" -> ExerciseType.CYCLING
            "SWIMMING" -> ExerciseType.SWIMMING
            "DANCING" -> ExerciseType.DANCING
            "BOXING" -> ExerciseType.BOXING
            "KICKBOXING" -> ExerciseType.KICKBOXING
            else -> ExerciseType.CHEST
        }
    }

    private fun onDeleteExercise(exercise: Exercise) {
        binding.exerciseMenuCard.visibility = View.GONE
        if (exercise.uid == helperDB.getFirebaseAuth().uid) {
            helperDB.exercisesSchema.child(exercise.eid!!).removeValue()
            Toast.makeText(this, "Exercise Eliminated", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "You can't delete this exercise", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}