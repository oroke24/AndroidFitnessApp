package com.example.fitnessapp
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : ComponentActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var exitButton: ImageButton
    private lateinit var seeButton: ImageButton
    private lateinit var auth: FirebaseAuth
    val fx = InteractionEffects()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        exitButton = findViewById(R.id.exitButton)
        seeButton = findViewById(R.id.seePasswordButton)

        exitButton.setOnClickListener{
            fx.imageButtonClickEffectQuick(exitButton)
            //finish()
            finishAffinity()
        }
        loginButton.setOnClickListener {
            fx.buttonClickEffect(loginButton)
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_LONG)
                    .show()
            }
        }
        seeButton.setOnClickListener{
            fx.imageButtonClickEffectQuick(seeButton)
            passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        registerButton.setOnClickListener {
            fx.buttonClickEffect(registerButton)
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("password", password)
            startActivity(intent)
        }
}

    private fun loginUser(email: String, password: String) {
        loginButton.visibility= View.GONE
        registerButton.visibility = View.GONE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if(user != null && user.isEmailVerified){
                        navigateToMain(email)
                    }else {
                        Toast.makeText(baseContext, "Must verify email after registration", Toast.LENGTH_LONG).show()
                        loginButton.visibility = View.VISIBLE
                        registerButton.visibility = View.VISIBLE
                        //val initializeData = InitializeData(email)
                        //initializeData.begin() //Only use here for database testing
                    }
                } else {
                    loginButton.visibility= View.VISIBLE
                    registerButton.visibility = View.VISIBLE
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun navigateToMain(email: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USER_EMAIL", email)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loginButton.visibility = View.VISIBLE
        registerButton.visibility = View.VISIBLE
    }
}