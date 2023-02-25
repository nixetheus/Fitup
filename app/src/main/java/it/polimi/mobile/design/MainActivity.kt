package it.polimi.mobile.design

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.polimi.mobile.design.databinding.ActivityMainBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mAuth = FirebaseAuth.getInstance()
    private var userDatabase = FirebaseDatabase.getInstance().getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUserSignIn()

        binding.SignInBtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.SignUpbtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkUserSignIn() {

        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth.currentUser != null) {

            val uid = mAuth.uid.toString()
            userDatabase.child(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    val intent = Intent(this, CentralActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}

/*private fun printHashKey(pContext: Context) {
try {
val info: PackageInfo = pContext.packageManager
.getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
for (signature in info.signatures) {
val md: MessageDigest = MessageDigest.getInstance("SHA")
md.update(signature.toByteArray())
val hashKey: String = String(Base64.encode(md.digest(), 0))
Log.i(TAG, "printHashKey() Hash Key: $hashKey")
}
} catch (e: NoSuchAlgorithmException) {
Log.e(TAG, "printHashKey()", e)
} catch (e: Exception) {
Log.e(TAG, "printHashKey()", e)
}
}*/


