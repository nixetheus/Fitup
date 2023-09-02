package it.polimi.mobile.design

import android.content.res.Resources
import android.view.KeyEvent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import it.polimi.mobile.design.entities.Workout
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WorkoutListActivityTest {

    private lateinit var activityScenario: ActivityScenario<WorkoutListActivity>

    @Before
    fun setUp() {
        Intents.init()
        activityScenario = ActivityScenario.launch(WorkoutListActivity::class.java)

        // Disable animations
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.executeShellCommand("settings put global window_animation_scale 0")
        device.executeShellCommand("settings put global transition_animation_scale 0")
        device.executeShellCommand("settings put global animator_duration_scale 0")
    }

    @After
    fun tearDown() {
        Intents.release()
        activityScenario.close()

        // Re-enable
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.executeShellCommand("settings put global window_animation_scale 1")
        device.executeShellCommand("settings put global transition_animation_scale 1")
        device.executeShellCommand("settings put global animator_duration_scale 1")
    }

    @Test
    fun testCreateWorkoutButton() {
        // Perform click on the create workout button
        onView(withId(R.id.addWorkoutsButton)).perform(click())
        // Verify that add workout card is visible
        onView(withId(R.id.addWorkoutCard)).check(matches(isDisplayed()))
    }

    @Test
    fun testShowWorkoutsAndClickOnWorkout() {

        val workoutName = "TEST WORKOUT"

        onView(withId(R.id.addWorkoutsButton)).perform(click())
        onView(withId(R.id.workoutNameField)).perform(typeText(workoutName), closeSoftKeyboard())
        onView(withId(R.id.confirmAddWorkoutBtn)).perform(click())
        onView(withId(R.id.workoutsListLayout)).perform(ViewActions.swipeUp())
        // verify that workouts are correctly visualized
        onView(withId(R.id.workoutsListLayout)).check(matches(isDisplayed()))
        Thread.sleep(2000)
        onView(allOf(withId(R.id.workoutDisplayNameList), withText(workoutName))).check(matches(isDisplayed()))

        // click on a workout
        onView(withText(workoutName)).perform(click())
        Thread.sleep(6000)

        // Verify that Workout Play Activity is started
        Intents.intended(IntentMatchers.hasComponent(WorkoutPlayActivity::class.java.name))

        // Go back
        pressBack()
        Thread.sleep(6000)

        // Delete test workouts
        onView(allOf(withId(R.id.workoutDisplayNameList), withText(workoutName))).perform(longClick())
        onView(withId(R.id.deleteWorkoutButton)).perform(click())

    }


    @Test
    fun testCreateWorkoutWithEmptyName() {
        // Perform click on the create workout button
        onView(withId(R.id.addWorkoutsButton)).perform(click())
        // Perform click on the confirm add workout button with an empty name field
        onView(withId(R.id.confirmAddWorkoutBtn)).perform(click())
        // Check if the Toast message is displayed correctly using ShadowToast
       // val shadowToast = Shadows.shadowOf(ShadowToast.getLatestToast())
        //assertEquals("Fill in all fields to continue!!", shadowToast.getText().toString())
    }

    @Test
    fun testSearch(){

        val testName1 = "Test1"
        val testName2 = "Test2"

        onView(withId(R.id.addWorkoutsButton)).perform(click())
        onView(withId(R.id.workoutNameField)).perform(typeText(testName2), closeSoftKeyboard())
        onView(withId(R.id.confirmAddWorkoutBtn)).perform(click())
        Thread.sleep(2000)

        onView(withId(R.id.addWorkoutsButton)).perform(click())
        onView(withId(R.id.workoutNameField))
            .perform(clearText(), typeText(testName1), closeSoftKeyboard())
        onView(withId(R.id.confirmAddWorkoutBtn)).perform(click())
        Thread.sleep(2000)
        
        // Search for workouts starting with "Test1" (e.g., "Test1")
        onView(withContentDescription("Search")).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.searchWorkout)).perform(typeText(testName1), closeSoftKeyboard())

        // Verify that only "Test1" is displayed
        onView(allOf(withId(R.id.workoutDisplayNameList), withText(testName1))).check(matches(isDisplayed()))
        onView(withText(testName2)).check(ViewAssertions.doesNotExist())

        // Setup search bar to delete workout
        onView(withId(R.id.searchWorkout)).perform(click())
        onView(withId(R.id.searchWorkout)).perform(pressKey(KeyEvent.KEYCODE_DEL))
        onView(withId(R.id.searchWorkout)).perform(closeSoftKeyboard())

        // Delete test workouts
        onView(allOf(withId(R.id.workoutDisplayNameList), withText(testName1))).perform(longClick())
        onView(withId(R.id.deleteWorkoutButton)).perform(click())
    }
}



