package com.example.fitnessapp
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : ComponentActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        val fx = InteractionEffects()
        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.email)
        emailEditText.setText(intent.getStringExtra("email"))
        passwordEditText = findViewById(R.id.password)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        backButton.setOnClickListener {
            fx.imageButtonClickEffect(backButton)
            finish()
        }
        registerButton.setOnClickListener {
            fx.buttonClickEffect(registerButton)
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        val user = auth.currentUser
        if (user != null && user.isEmailVerified) {
            // User is already registered and email is verified
            Toast.makeText(
                baseContext, "You are already registered and verified.",
                Toast.LENGTH_LONG
            ).show()
            // You may choose to navigate the user to another screen or perform any other action here
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        sendVerificationEmail(email)
                        val initializeData = InitializeData(email)
                        initializeData.begin()
                        //startActivity(Intent(this, LoginActivity::class.java))
                        Toast.makeText(
                            baseContext, "Registration successful. Please verify your email.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // Registration failed, displaying a message to the user
                        Toast.makeText(
                            baseContext, "User exists, sending verification link to email",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    finish()
                }
        }
    }

    /*
    private fun registerUser(email: String, password: String) {
        val user = auth.currentUser
        if(user != null) {
            Toast.makeText(baseContext, "user already exists.", Toast.LENGTH_LONG).show()
        }else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        sendVerificationEmail(email)
                        val initializeData = InitializeData(email)
                        initializeData.begin()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        // Registration failed, displaying a message to the user
                        Toast.makeText(
                            baseContext, "$email already has account",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

     */
    private fun sendVerificationEmail(email: String) {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext, "Verify link sent to $email (resets in 24hrs)",
                        Toast.LENGTH_LONG + 5
                    ).show()
                } else {
                    Toast.makeText(
                        baseContext, "Failed to send verification email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}