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
        val initializeData = InitializeData(email)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    initializeData.begin()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    // Registration failed, displaying a message to the user
                    Toast.makeText(baseContext, "Registration failed or user already exists.",
                        Toast.LENGTH_LONG).show()
                }
            }
    }
}