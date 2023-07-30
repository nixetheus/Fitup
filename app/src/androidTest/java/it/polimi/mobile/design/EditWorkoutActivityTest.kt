package it.polimi.mobile.design

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import it.polimi.mobile.design.entities.Workout
import kotlinx.coroutines.delay
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Math.abs
import kotlin.random.Random
import kotlin.random.nextInt

@RunWith(AndroidJUnit4::class)
class EditWorkoutActivityTest {

    private lateinit var activityScenario: ActivityScenario<EditWorkoutActivity>

    @Before
    fun setup() {

        Intents.init()

        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, EditWorkoutActivity::class.java)
        intent.putExtra("Workout", createFakeWorkout())

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
        Thread.sleep(1000)
        onView(withId(R.id.addExerciseToWorkoutCard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun testCloseAddExerciseLayout() {
        // Perform click on the plus exercise button
        onView(withId(R.id.openAddExerciseLayout)).perform(click())
        // Verify that add exercise layout is visible
        Thread.sleep(500)
        onView(withId(R.id.addExerciseToWorkoutCard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        // Perform click on close button
        onView(withId(R.id.addExerciseWorkoutClose)).perform(click())
        // Verify that add exercise layout is gone
        Thread.sleep(500)
        onView(withId(R.id.addExerciseToWorkoutCard)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun addExerciseTest() {
        /*val randomSets = (kotlin.math.abs(Random.nextInt()) + 100).toString()
        // Perform click on the plus exercise button
        onView(withId(R.id.openAddExerciseLayout)).perform(click())
        // Fill Data
        onView(withId(R.id.setsInputValue)).perform(typeText(randomSets), closeSoftKeyboard())
        onView(withId(R.id.repsInputValue)).perform(typeText("1"), closeSoftKeyboard())
        onView(withId(R.id.restInputValue)).perform(typeText("90"), closeSoftKeyboard())
        onView(withId(R.id.weightInputValue)).perform(typeText("0"), closeSoftKeyboard())
        onView(withId(R.id.bufferInputValue)).perform(typeText("0"), closeSoftKeyboard())
        onView(withId(R.id.confirmAddWorkoutBtn)).perform(click())
        // Verify that exercise was added
        Thread.sleep(2000)
        onView(withChild(allOf(withId(R.id.setsValue), withText(randomSets)))).check(matches(isDisplayed()))*/
        assert(true)  // TODO
    }

    // Navigation Tests
    @Test
    fun testClickOnHome() {
        // Perform click on the home button
        onView(withId(R.id.homeButton)).perform(click())
        // Verify that Central Activity is started
        Intents.intended(IntentMatchers.hasComponent(CentralActivity::class.java.name))
    }
}
