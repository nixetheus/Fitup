package it.polimi.mobile.design

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInActivityTest {

    private lateinit var activityScenario: ActivityScenario<SignInActivity>

    @Before
    fun setup() {
        Intents.init()
        activityScenario = ActivityScenario.launch(Intent(ApplicationProvider.getApplicationContext(), SignInActivity::class.java))
    }

    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }



    @Test
    fun testSignInWithEmailPassword_Failure_andThen() {


        Thread.sleep(2000)

        // Inserisci email e password errati
        onView(withId(R.id.EmailField)).perform(replaceText("wrong@example.com"))
        onView(withId(R.id.Pass)).perform(replaceText("wrongpassword"))

        // Chiudi la tastiera
        onView(withId(R.id.Pass)).perform(closeSoftKeyboard())

        // Fai clic sul pulsante di accesso
        onView(withId(R.id.emailLoginButton)).perform(click())


        assert(activityScenario.state == Lifecycle.State.RESUMED)

        Thread.sleep(10000)
        // Inserisci email e password validi
        onView(withId(R.id.EmailField)).perform(replaceText("test@example.com"))
        onView(withId(R.id.Pass)).perform(replaceText("password"))

        // Chiudi la tastiera
        onView(withId(R.id.Pass)).perform(closeSoftKeyboard())

        // Fai clic sul pulsante di accesso
        onView(withId(R.id.emailLoginButton)).perform(click())
        Thread.sleep(10000)

        Intents.intended(IntentMatchers.hasComponent(CentralActivity::class.java.name))
    }

}
