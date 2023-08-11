package it.polimi.mobile.design

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import it.polimi.mobile.design.databinding.ActivitySignUpBinding
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowToast
import java.lang.reflect.Field

@RunWith(AndroidJUnit4::class)
class SignUpActivityUnitTest {

    private lateinit var signUpActivity: SignUpActivity

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        signUpActivity = SignUpActivity()
        signUpActivity.setFirebaseAuth(mockAuth)
    }
    @Test
    fun testSignUpSuccess() {
        val email = "test@example.com"
        val password = "password123"

        // Crea un mock di Task<AuthResult>

        val mockAuthResult = mock(AuthResult::class.java)
        val mockTask = mock(Task::class.java) as Task<AuthResult>
        `when`(mockTask.isSuccessful).thenReturn(true)

        `when`(mockAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockTask)

        val binding = ActivitySignUpBinding.inflate(signUpActivity.layoutInflater)

        // Utilizza reflection per accedere alla variabile privata "binding"
        val bindingField: Field = signUpActivity::class.java.getDeclaredField("binding")
        bindingField.isAccessible = true
        bindingField.set(signUpActivity, binding)

        binding.EmailField.setText(email)
        binding.Pass.setText(password)
        binding.Pass2.setText(password)

        binding.SignUpBtn.performClick()

        // Verifica che l'intento sia avviato correttamente
        val expectedIntent = Intent(signUpActivity, HomeActivity::class.java)
        val actualIntent = Shadows.shadowOf(signUpActivity).peekNextStartedActivity()
        assertEquals(expectedIntent.component, actualIntent.component)
    }

    @Test
    fun testSignUpFailure() {
        val email = "test@example.com"
        val password = "invalid_password"

        `when`(mockAuth.createUserWithEmailAndPassword(email, password)).thenThrow(
            FirebaseAuthException("error_code", "Errore di registrazione")
        )

        val binding = ActivitySignUpBinding.inflate(signUpActivity.layoutInflater)
        val bindingField: Field = signUpActivity::class.java.getDeclaredField("binding")
        bindingField.isAccessible = true
        bindingField.set(signUpActivity, binding)

        binding.EmailField.setText(email)
        binding.Pass.setText(password)
        binding.Pass2.setText(password)

        binding.SignUpBtn.performClick()

        // Verifica che venga mostrato un messaggio di errore
        val toastMessage = ShadowToast.getTextOfLatestToast()
        assertEquals("Authentication failed. Please try again.", toastMessage)
    }


}
