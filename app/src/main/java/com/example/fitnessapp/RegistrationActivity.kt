package com.example.fitnessapp
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : ComponentActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var seeButton: ImageButton
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth
    val fx = InteractionEffects()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        auth = FirebaseAuth.getInstance()
        backButton = findViewById(R.id.backButton)
        emailEditText = findViewById(R.id.email)
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.confirmPassword)
        seeButton = findViewById(R.id.seePasswordButton)
        emailEditText.setText(intent.getStringExtra("email"))
        passwordEditText.setText(intent.getStringExtra("password"))
        registerButton = findViewById(R.id.registerButton)

        backButton.setOnClickListener {
            fx.imageButtonClickEffect(backButton)
            finish()
        }
        seeButton.setOnClickListener{
           fx.imageButtonClickEffectQuick(seeButton)
            passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            confirmPasswordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        registerButton.setOnClickListener {
            fx.buttonClickEffect(registerButton)
            val email = emailEditText.text.toString()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val userProfile = UserProfile(email, username)
            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                if(confirmPassword != password) {
                    passwordEditText.setText("")
                    confirmPasswordEditText.setText("")
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show()
                }
                else registerUser(email, password, userProfile)
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerUser(email: String, password: String, userProfile: UserProfile) {
        val user = auth.currentUser
        if (user != null && user.isEmailVerified) {
            Toast.makeText(baseContext, "You are already registered and verified.", Toast.LENGTH_LONG).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        sendVerificationEmail(email)
                        val initializeData = InitializeData(userProfile)
                        initializeData.begin()
                        Toast.makeText(baseContext, "Registration successful. Please verify your email.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(baseContext, "User exists, sending verification link to email", Toast.LENGTH_LONG).show()
                    }
                    finish()
                }
        }
    }

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