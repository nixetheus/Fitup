package it.polimi.mobile.design

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import it.polimi.mobile.design.databinding.ActivitySignInBinding
import it.polimi.mobile.design.helpers.DatabaseHelper


class SignInActivity : AppCompatActivity() {

    var auth =  FirebaseAuth.getInstance()
    private val helperDB = DatabaseHelper().getInstance()
    private lateinit var binding: ActivitySignInBinding
    private lateinit var buttonFacebookLogin: LoginButton
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var callbackManager = CallbackManager.Factory.create()

    private val RC_SIGN_IN: Int = 0
    private lateinit var gso : GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createBindings()

        buttonFacebookLogin = binding.facebookLogin
        buttonFacebookLogin.setPermissions("email", "public_profile")
        createRequest()
    }

    private fun createBindings() {

        binding.Pass.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.action == KeyEvent.ACTION_DOWN) {
                binding.emailLoginButton.performClick()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.signupLogin.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.buttonGoogle2.setOnClickListener {
            googleSignIn()
        }

        binding.forgetPass.setOnClickListener{
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.emailLoginButton.setOnClickListener {
            val pass = binding.Pass.text.toString()
            val email = binding.EmailField.text.toString()
            logIn(pass, email)
        }

        binding.facebookLogin.
        registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })
    }

    private fun logIn(pass: String, email: String) {
        if (email.isNotEmpty() && pass.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.uid.toString()
                    helperDB.usersSchema.child(uid).get().addOnSuccessListener {
                        if (it.exists())
                        {
                            val intent = Intent(this, CentralActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please, fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null)
            updateUI(currentUser)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null)
        {
            val uid = auth.uid.toString()
            helperDB.usersSchema.child(uid).get().addOnSuccessListener {
                if (it.exists())
                {
                    val intent = Intent(this, CentralActivity::class.java)
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        else {val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)}
    }

    private fun createRequest() {
        // Configure Google Sign In
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso)
    }

    // TODO: modernize
    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data)
            task.exception

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            }
            catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val uid = auth.uid.toString()
                    helperDB.usersSchema.child(uid).get().addOnSuccessListener {
                        if (it.exists())
                        {
                            val intent = Intent(this, CentralActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@SignInActivity, "Login Failed: ", Toast.LENGTH_SHORT).show()
                }
            }
    }
}