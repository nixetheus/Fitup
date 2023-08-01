package it.polimi.mobile.design

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue
import it.polimi.mobile.design.entities.User
import it.polimi.mobile.design.enum.Gender
import org.junit.After
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29], manifest = Config.NONE)
class SignUpActivityTest{

    private lateinit var context: Context
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    @Before
    fun setUp() {
        // Initialize Firebase
        context = ApplicationProvider.getApplicationContext()
        FirebaseApp.initializeApp(context)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        // Set up any additional configurations for Firebase authentication and database
        // For example, you may want to use Firebase Test Lab to create a separate test database
        // and configure the Firebase instances accordingly.
    }

  /*  @After
    fun tearDown() {
        // Clean up Firebase resources
        //firebaseDatabase.reference.child("Users").child(getCurrentUserId()).removeValue()
        //firebaseAuth.signOut()
    }*/

    @Test
    fun testCreateUserAndVerifyPresence() {
        val email = "test@example.com"
        val password = "password"

        // Create a user in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                assertTrue("User creation successful", task.isSuccessful)

                val newUser = User(firebaseAuth.currentUser?.uid, "test", 10f, 20 ,Gender.FEMALE, 0F)
                firebaseAuth.currentUser?.uid?.let { firebaseDatabase.reference.child(it).setValue(newUser).addOnSuccessListener {} }
                // Verify the user's presence in the Firebase Realtime Database
                val userId = getCurrentUserId()
                firebaseDatabase.reference.child("Users").child(userId).get()
                    .addOnCompleteListener { dbTask ->
                        assertTrue("User is present in the database", dbTask.isSuccessful)
                        assertTrue("User exists in the database", dbTask.result.exists())
                    }
            }
    }

    private fun getCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User is not signed in")
    }
}