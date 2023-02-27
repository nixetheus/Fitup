package it.polimi.mobile.design

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.polimi.mobile.design.databinding.ActivityExerciseListBinding
import it.polimi.mobile.design.databinding.FragmentExerciseListBinding
import it.polimi.mobile.design.databinding.FragmentWorkoutBinding
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
            
            // Setup
            val set = ConstraintSet()
            exerciseMenu.id = View.generateViewId()
            exerciseLayout.addView(exerciseMenu, 0)
            
            set.clone(exerciseLayout)
            set.connect(exerciseMenu.id, ConstraintSet.TOP, exerciseLayout.id, ConstraintSet.TOP, 0)
            set.connect(exerciseMenu.id, ConstraintSet.BOTTOM, exerciseLayout.id, ConstraintSet.BOTTOM, 0)
            set.connect(exerciseMenu.id, ConstraintSet.LEFT, exerciseLayout.id, ConstraintSet.LEFT, 0)
            set.connect(exerciseMenu.id, ConstraintSet.RIGHT, exerciseLayout.id, ConstraintSet.RIGHT, 0)
            set.applyTo(exerciseLayout)

            binding.exercisesListLayout.addView(exerciseLayout)
        }
    }

    private fun createExerciseCard(): CardView {

        val exerciseCard = CardView(applicationContext)

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT)

        exerciseCard.radius = 15.toPx().toFloat()
        params.bottomMargin = 20.toPx()
        exerciseCard.layoutParams = params

        return exerciseCard

    }

    private fun createExerciseCardConstraintLayout(): ConstraintLayout {
        val exerciseLayout = ConstraintLayout(applicationContext)
        exerciseLayout.layoutParams = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        return exerciseLayout
    }

    private fun createExerciseImage(): ImageView {
        return ImageView(applicationContext)
        // TODO
    }

    private fun createExerciseMenuLinearLayout(): LinearLayout {
        val exerciseMenuLayout = LinearLayout(applicationContext)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        exerciseMenuLayout.background = ColorDrawable(Color.BLUE)
        exerciseMenuLayout.setPadding(20.toPx())
        exerciseMenuLayout.layoutParams = layoutParams
        exerciseMenuLayout.orientation = LinearLayout.VERTICAL
        return exerciseMenuLayout
    }

    private fun createExerciseNameText(exercise: Exercise): TextView {

        val exerciseNameView = TextView(applicationContext)
        exerciseNameView.text = exercise.name
        exerciseNameView.setTextColor(Color.WHITE)
        exerciseNameView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        exerciseNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        exerciseNameView.typeface = Typeface.create("Lato Bold", Typeface.BOLD)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        exerciseNameView.layoutParams = params

        return exerciseNameView
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}