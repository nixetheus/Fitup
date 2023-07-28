package it.polimi.mobile.design

import android.content.Intent
import android.view.View
import android.view.View.VISIBLE
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import it.polimi.mobile.design.entities.Workout
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditWorkoutActivityTest {

    private lateinit var activityScenario: ActivityScenario<EditWorkoutActivity>

    @Before
    fun setup() {

        Intents.init()

        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, EditWorkoutActivity::class.java)
        intent.putExtra("workout", createFakeWorkout())
        intent.putExtra("exp", 0)

        activityScenario = ActivityScenario.launch(intent)
    }

    private fun createFakeWorkout() : Workout {
        return Workout(
            "-Na7Mh7plP_oDVrlVw-c",
            "Mw19C5PhigZGoG1OtwpTt8BF1op1",
            "Strength - Total Body 1",
            "hip hop",
            -4,
            1690447497966,
            mutableListOf(1, 1, 0, 0)
        )
    }

    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testOpenAddExerciseLayout() {
        // Perform click on the plus exercise button
        onView(withId(R.id.openAddExerciseLayout)).perform(click())
        // Verify that add exercise layout is visible
        onView(withId(R.id.addExerciseToWorkoutCard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun testCloseAddExerciseLayout() {
        // Perform click on the plus exercise button
        onView(withId(R.id.openAddExerciseLayout)).perform(click())
        // Verify that add exercise layout is visible
        onView(withId(R.id.addExerciseToWorkoutCard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        // Perform click on close button
        onView(withId(R.id.addExerciseWorkoutClose)).perform(click())
        // Verify that add exercise layout is gone
        onView(withId(R.id.addExerciseToWorkoutCard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    // TODO? Add Exercise Test?

    // Navigation Tests
    @Test
    fun testClickOnHome() {
        // Perform click on the home button
        onView(withId(R.id.homeButton)).perform(click())
        // Verify that Central Activity is started
        Intents.intended(IntentMatchers.hasComponent(CentralActivity::class.java.name))
    }
}
