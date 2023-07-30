package it.polimi.mobile.design

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogoutPopUpTest {

    private lateinit var activityScenario: ActivityScenario<LogoutPopUp>

    @Before
    fun setup() {
        Intents.init()
        activityScenario = ActivityScenario.launch(LogoutPopUp::class.java)
    }

    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testClickOnNoButton() {
        // Perform click on no button
        onView(withId(R.id.noBtn)).perform(click())
        Intents.intended(hasComponent(CentralActivity::class.java.name))
    }
}
