package com.example.fitnessapp
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    /*Redacting Guest Button for now
    private lateinit var guestButton: Buttonw
     */

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val username = currentUser?.email.toString()
        if(currentUser != null){
            navigateToMain(username)
        }

        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        /*Redacting guest button for now
    guestButton = findViewById(R.id.guestButton)
    */

        loginButton.setOnClickListener {
            loginButton.alpha = 0.5f
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT)
                    .show()
            }
            // Revert to the original alpha value after a short delay
            Handler().postDelayed({
                loginButton.alpha = 1.0f
            }, 2000)
        }

        // Set OnClickListener for the registration button
        registerButton.setOnClickListener {
            // Start the RegistrationActivity
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        /*Redacting guest button for now
    // Set OnClickListener for continue as guest button
    guestButton.setOnClickListener {
    // Start the MainActivity
    startActivity(Intent(this, MainActivity::class.java))
    }
    */

}

    private fun loginUser(email: String, password: String) {
        val initializeData = InitializeData()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success!
                    initializeData.addEmailToUsersIfNotExists(email)
                    navigateToMain(email)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToMain(email: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USER_EMAIL", email)
        startActivity(intent)
    }

}