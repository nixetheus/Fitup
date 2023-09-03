package it.polimi.mobile.design

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import it.polimi.mobile.design.enum.ExerciseType
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Method

@RunWith(AndroidJUnit4::class)
class ExerciseListActivityTest {

    private lateinit var activityScenario: ActivityScenario<ExerciseListActivity>

    @Before
    fun setup() {
        Intents.init()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, ExerciseListActivity::class.java)
        activityScenario = ActivityScenario.launch(intent)
    }

    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testOpenAddExerciseLayout() {
        // Perform click on the plus exercise button
        onView(withId(R.id.addExerciseButton)).perform(click())
        // Verify that add exercise layout is visible
        Thread.sleep(1000)
        onView(withId(R.id.addExerciseCard)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun testCloseAddExerciseLayout() {
        // Perform click on the plus exercise button
        onView(withId(R.id.addExerciseButton)).perform(click())
        // Verify that add exercise layout is visible
        Thread.sleep(1000)
        onView(withId(R.id.addExerciseCard)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        // Perform click on the close button
        onView(withId(R.id.addExerciseClose)).perform(click())
        Thread.sleep(1000)
        // Verify that add exercise layout is visible
        onView(withId(R.id.addExerciseCard)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun testOpenDeleteExerciseLayout() {
        Thread.sleep(1000)
        // Perform click on first exercise
        onView(allOf(withId(R.id.exerciseCard), withTagValue(`is`(0 as Any)))).perform(longClick())
        // Verify that delete exercise layout is visible
        Thread.sleep(1000)
        onView(withId(R.id.exerciseMenuCard)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun testCloseDeleteExerciseLayout() {
        Thread.sleep(1000)
        // Perform click on first exercise
        onView(allOf(withId(R.id.exerciseCard), withTagValue(`is`(0 as Any)))).perform(longClick())
        // Verify that delete exercise layout is visible
        Thread.sleep(1000)
        onView(withId(R.id.exerciseMenuCard)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        // Perform click on the close button
        onView(withId(R.id.closeExerciseMenu)).perform(click())
        Thread.sleep(1000)
        // Verify that delete exercise layout is visible
        onView(withId(R.id.exerciseMenuCard)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun testInsertWrongData() {
        Thread.sleep(1000)
        // Perform click on first exercise
        onView(withId(R.id.addExerciseButton)).perform(longClick())
        Thread.sleep(1000)
        // Insert wrong data
        onView(withId(R.id.kcalInputValue)).perform(typeText("WRONG"), closeSoftKeyboard())
        onView(withId(R.id.expInputValue)).perform(typeText("WRONG"), closeSoftKeyboard())
        // Verify no data was written
        onView(withId(R.id.kcalInputValue)).check(matches(withText("")))
        onView(withId(R.id.expInputValue)).check(matches(withText("")))
    }

    @Test
    fun testExerciseTypeCorrect() {
        val method: Method =
            ExerciseListActivity::class.java.getDeclaredMethod("exerciseTypeFromString", String::class.java)
        method.isAccessible = true

        // Normal case
        activityScenario.onActivity {
            val type = method.invoke(it, "SHOULDERS")
            Assert.assertEquals(type, ExerciseType.SHOULDERS)
        }

        // Edge case
        activityScenario.onActivity {
            val type = method.invoke(it, "GIBBERISH")
            Assert.assertEquals(type, ExerciseType.CHEST)
        }
    }

    // Navigation Tests
    @Test
    fun testClickOnHome() {
        Thread.sleep(2000)
        // Perform click on the home button
        onView(withId(R.id.homeButton)).perform(click())
        // Verify that Central Activity is started
        Thread.sleep(2000)
        Intents.intended(IntentMatchers.hasComponent(CentralActivity::class.java.name))
    }
}
