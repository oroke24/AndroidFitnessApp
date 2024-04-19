package com.example.fitnessapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileActivity: ComponentActivity(){
    private lateinit var backButton: ImageButton
    private lateinit var emailTextView: TextView
    private lateinit var usernameEditText: EditText
    private lateinit var changeUsernameButton: Button
    private lateinit var userProfileDataManager: UserProfileDataManager
    private lateinit var fx: InteractionEffects

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val email = intent.getStringExtra("USER_EMAIL")?:"no user named"
        fx = InteractionEffects()

        backButton = findViewById(R.id.backButton)
        emailTextView = findViewById(R.id.emailTextView)
        usernameEditText = findViewById(R.id.usernameEditText)
        changeUsernameButton = findViewById(R.id.changeUsernameButton)
        userProfileDataManager = UserProfileDataManager(email)

        backButton.setOnClickListener{ finish() }
        emailTextView.text = email

        CoroutineScope(Dispatchers.Main).launch {
            usernameEditText.setText(userProfileDataManager.getUsername())
        }
        changeUsernameButton.setOnClickListener {
            fx.buttonClickEffect(changeUsernameButton)
            val newName = usernameEditText.text.toString()
            userProfileDataManager.updateUsername(newName)
            Toast.makeText(this, "Name updated to $newName", Toast.LENGTH_LONG).show()
        }
    }
}