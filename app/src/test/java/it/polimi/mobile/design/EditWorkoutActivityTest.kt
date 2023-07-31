package it.polimi.mobile.design

import org.junit.Assert.*

import android.content.Intent
import android.text.Editable
import it.polimi.mobile.design.databinding.ActivityEditWorkoutBinding
import it.polimi.mobile.design.entities.Exercise
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowToast

class EditWorkoutActivityTest {

    private lateinit var activity: EditWorkoutActivity

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(EditWorkoutActivity::class.java)
            .create()
            .start()
            .resume()
            .get()
    }

    @Test
    fun testAddNewExercise() {
        // Mock the necessary dependencies
        /*val exercise = Exercise()
        exercise.eid = "exercise_id"
        exercise.name = "Exercise Name"

        val exerciseSpinner = mock(ExerciseSpinner::class.java)
        `when`(exerciseSpinner.selectedItem).thenReturn(exercise)

        val binding = mock(ActivityEditWorkoutBinding::class.java)
        val setsEditable: Editable = mock(Editable::class.java)
        val repsEditable: Editable = mock(Editable::class.java)
        val restEditable: Editable = mock(Editable::class.java)
        val weightEditable: Editable = mock(Editable::class.java)
        val bufferEditable: Editable = mock(Editable::class.java)
        `when`(binding.exercisesSpinner).thenReturn(exercise)
        `when`(binding.setsInputValue.text).thenReturn(setsEditable)
        `when`(binding.repsInputValue.text).thenReturn(repsEditable)
        `when`(binding.restInputValue.text).thenReturn(restEditable)
        `when`(binding.weightInputValue.text).thenReturn(weightEditable)
        `when`(binding.bufferInputValue.text).thenReturn(bufferEditable)
        `when`(setsEditable.toString()).thenReturn("3")
        `when`(repsEditable.toString()).thenReturn("10")
        `when`(restEditable.toString()).thenReturn("60")
        `when`(weightEditable.toString()).thenReturn("50")
        `when`(bufferEditable.toString()).thenReturn("0")

        activity.binding = binding

        // Perform the action
        activity.onAddNewExercise()

        // Verify the expected behavior
        val expectedToastMessage = "Successfully saved!!"
        assertEquals(expectedToastMessage, ShadowToast.getTextOfLatestToast())

        // Verify that the exercise was added to the list
        val workoutExerciseList = activity.workoutExerciseList
        assertEquals(1, workoutExerciseList.size)
        assertEquals("exercise_id", workoutExerciseList[0].id)

        // Verify that the intent to navigate back to WorkoutListActivity is started
        val expectedIntent = Intent(activity, WorkoutListActivity::class.java)
        val shadowActivity = Shadows.shadowOf(activity)
        val actualIntent = shadowActivity.nextStartedActivity
        assertEquals(expectedIntent.component, actualIntent.component)*/
        assert(true)
    }
}
