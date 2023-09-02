package it.polimi.mobile.design



import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class SignUpActivityTest {

    private lateinit var activityScenario: ActivityScenario<SignUpActivity>
    private lateinit var mockAuth: FirebaseAuth

    @Before
    fun setup() {
        Intents.init()
        mockAuth = mock(FirebaseAuth::class.java)
        activityScenario = ActivityScenario.launch(Intent(ApplicationProvider.getApplicationContext(), SignUpActivity::class.java))
    }
    @After
    fun cleanup() {
        Intents.release()
        activityScenario.close()
    }

    @Test
    fun testSignUpSuccessButEmailAlreadyUsed() {
        // insert email and password
        onView(withId(R.id.EmailField)).perform(typeText("test@example111.com"))
        onView(withId(R.id.Pass)).perform(typeText("password"))
        onView(withId(R.id.Pass2)).perform(typeText("password"))
        onView(withId(R.id.Pass)).perform(ViewActions.closeSoftKeyboard())

        // Fai clic sul pulsante di registrazione
        onView(withId(R.id.SignUpBtn)).perform(click())
        Thread.sleep(4000)


        // Verify that activity does not change
        assert(activityScenario.state == Lifecycle.State.RESUMED)
        Thread.sleep(4000)
    }

    @Test
    fun testSignUpPasswordMismatch() {
        // Iinsert email and password
        onView(withId(R.id.EmailField)).perform(typeText("test@example.com"))
        onView(withId(R.id.Pass)).perform(typeText("password"))
        onView(withId(R.id.Pass2)).perform(typeText("differentPassword"))
        onView(withId(R.id.Pass)).perform(ViewActions.closeSoftKeyboard())

        onView(withId(R.id.SignUpBtn)).perform(click())


        // Verify that activity does not change
        Intents.intended(IntentMatchers.hasComponent(SignUpActivity::class.java.name))
    }

    @Test
    fun testSignUpEmptyFields() {
        //empty field
        onView(withId(R.id.SignUpBtn)).perform(click())


        // Verify that activity does not change
        assert(activityScenario.state == Lifecycle.State.RESUMED)
    }
}
