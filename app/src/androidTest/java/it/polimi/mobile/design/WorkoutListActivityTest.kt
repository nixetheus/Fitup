package it.polimi.mobile.design

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.not
import it.polimi.mobile.design.entities.Workout
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
    }

    @After
    fun tearDown() {
        Intents.release()
        activityScenario.close()
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

        onView(withId(R.id.addWorkoutsButton)).perform(click())
        onView(withId(R.id.workoutNameField)).perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.confirmAddWorkoutBtn)).perform(click())
        // verify that workouts are correctly visualized
        onView(withId(R.id.workoutsListLayout)).check(matches(isDisplayed()))
        Thread.sleep(2000)
        onView(withText("Test")).check(matches(isDisplayed()))


        // click on a workout
        onView(withText("Test")).perform(click())
        Thread.sleep(6000)

        // Verify that Workout Play Activity is started
        Intents.intended(IntentMatchers.hasComponent(WorkoutPlayActivity::class.java.name))

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

    // TODO: Add more tests for other functionalities and interactions

    // SpinnerTest
  @Test
  fun testSpinner(){
        onView(withId(R.id.addWorkoutsButton)).perform(click())
        onView(withId(R.id.workoutNameField)).perform(typeText("test2"), closeSoftKeyboard())
        onView(withId(R.id.confirmAddWorkoutBtn)).perform(click())
        // verify that workouts are correctly visualized
        onView(withId(R.id.workoutsListLayout)).check(matches(isDisplayed()))
        Thread.sleep(2000)
        onView(withText("Test2")).check(matches(isDisplayed()))

        onView(withId(R.id.addWorkoutsButton)).perform(click())
        onView(withId(R.id.workoutNameField))
            .perform(clearText(), typeText("1test"), closeSoftKeyboard())
        onView(withId(R.id.confirmAddWorkoutBtn)).perform(click())
        // verify that workouts are correctly visualized
        onView(withId(R.id.workoutsListLayout)).check(matches(isDisplayed()))
        Thread.sleep(2000)
        // Search for workouts starting with "1" (e.g., "Test1")
        onView(withId(R.id.searchWorkout)).perform(click()).perform(typeText("1"), closeSoftKeyboard())

        // Verify that only "Test1" is displayed
        onView(withId(R.id.workoutsListLayout)).check(matches(isDisplayed()))
        onView(withText("Test1")).check(matches(isDisplayed()))
        onView(withText("Test2")).check(matches(not(isDisplayed())))






    }  }



