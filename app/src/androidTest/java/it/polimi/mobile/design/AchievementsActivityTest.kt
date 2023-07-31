package it.polimi.mobile.design

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.polimi.mobile.design.entities.Achievement
import it.polimi.mobile.design.entities.UserAchievements
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Integer.MAX_VALUE
import java.lang.reflect.Method

@RunWith(AndroidJUnit4::class)
class AchievementsActivityTest {

    private lateinit var activityScenario: ActivityScenario<AchievementsActivity>

    @Before
    fun setup() {
        Intents.init()
        activityScenario = ActivityScenario.launch(AchievementsActivity::class.java)
    }

    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testDisplayCompletedText() {

        Thread.sleep(1500)

        val method: Method =
            AchievementsActivity::class.java.getDeclaredMethod("displayCompletedText", List::class.java, List::class.java)
        method.isAccessible = true

        activityScenario.onActivity {
            method.invoke(it, emptyList<Achievement>(), emptyList<UserAchievements>())
        }
        onView(withId(R.id.achievementsNumberValue)).check(matches(withText("0/0")))

        activityScenario.onActivity {
            method.invoke(it, listOf(Achievement()), emptyList<UserAchievements>())
        }
        onView(withId(R.id.achievementsNumberValue)).check(matches(withText("0/1")))
    }

    @Test
    fun testColorAchievement() {

        val method: Method =
            AchievementsActivity::class.java.getDeclaredMethod("getAchievementColor", Int::class.java, Int::class.java)
        method.isAccessible = true

        // Normal case
        activityScenario.onActivity {
            val colorTestOne = method.invoke(it, 0, 0)
            Assert.assertEquals(colorTestOne, it.resources.getColor(R.color.gold, it.applicationContext.theme))
        }

        // Edge case
        activityScenario.onActivity {
            val colorTestOne = method.invoke(it, MAX_VALUE, 0)
            Assert.assertEquals(colorTestOne, it.resources.getColor(android.R.color.transparent, it.applicationContext.theme))
        }

    }

    @Test
    fun testClickOnHomeButtonShouldStartCentralActivity() {
        // Perform click on the home button
        onView(withId(R.id.homeButton)).perform(click())
        // Verify that CentralActivity is started
        Intents.intended(hasComponent(CentralActivity::class.java.name))
    }
}
