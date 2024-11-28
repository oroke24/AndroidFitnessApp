package com.example.fitnessapp.userProfile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.fitnessapp.InteractionEffects
import com.example.fitnessapp.R
import com.example.fitnessapp.firestore.UserProfileDataManager
import com.example.fitnessapp.startup.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileActivity: ComponentActivity(){
    private lateinit var backButton: ImageButton
    private lateinit var emailTextView: TextView
    private lateinit var usernameEditText: EditText
    private lateinit var changeUsernameButton: Button
    private lateinit var commentsEditText: EditText
    private lateinit var commentsButton: Button
    private lateinit var deleteAccountButton: Button
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
        commentsEditText = findViewById(R.id.commentEditText)
        commentsButton = findViewById(R.id.commentButton)
        deleteAccountButton = findViewById(R.id.deleteAccountButton)
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
        commentsButton.setOnClickListener {
            fx.buttonClickEffect(commentsButton)
            val newComment = commentsEditText.text.toString()
            if(commentsEditText.text.isNotBlank()) userProfileDataManager.addComment(newComment)
            else Toast.makeText(this, "Comment is blank", Toast.LENGTH_SHORT).show()
            commentsEditText.text.clear()
            Toast.makeText(this, "Thanks for the comment!", Toast.LENGTH_SHORT).show()
        }
        deleteAccountButton.setOnLongClickListener {
            val context = this
            CoroutineScope(Dispatchers.Main).launch {
                val deleteAccountForSure = fx.userApproveGeneral(context, "Delete all information and remove account?")
                if(deleteAccountForSure){
                    userProfileDataManager.deleteAccount()
                    val user = FirebaseAuth.getInstance().currentUser
                    finishAffinity()
                    user?.delete()
                    Toast.makeText(context, "Account Deleted", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context, LoginActivity::class.java))
                }
                else Toast.makeText(context, "Account Not Deleted.", Toast.LENGTH_LONG).show()
            }
            true
        }
    }
}