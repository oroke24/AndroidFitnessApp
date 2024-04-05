package com.example.fitnessapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity



class ItemDetailsActivity : ComponentActivity() {
    private lateinit var mainLayout: LinearLayout
    private lateinit var backButton : ImageButton
    private lateinit var titleTextView: TextView
    private lateinit var subGroupOneTitleTextView: TextView
    private lateinit var subGroupOneTextView: TextView
    private lateinit var subGroupTwoTitleTextView: TextView
    private lateinit var subGroupTwoTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        mainLayout = findViewById(R.id.mainLayout)
        backButton = findViewById(R.id.backButton)
        titleTextView = findViewById(R.id.title)
        subGroupOneTitleTextView = findViewById(R.id.subGroupOneTitle)
        subGroupOneTextView = findViewById(R.id.subGroupOne)
        subGroupTwoTitleTextView = findViewById(R.id.subGroupTwoTitle)
        subGroupTwoTextView = findViewById(R.id.subGroupTwo)

        backButton.setOnClickListener{finish()}
        // Retrieve data passed from ExerciseAdapter and display it and
        // Use the data to populate the views in activity_item_details.xml
        val mainBackground = intent.getIntExtra("backgroundID", -1)
        titleTextView.text = intent.getStringExtra("title")
        subGroupOneTitleTextView.text = intent.getStringExtra("subOneTitle")
        subGroupOneTextView.text = intent.getStringExtra("subOneDetails")
        subGroupTwoTitleTextView.text = intent.getStringExtra("subTwoTitle")
        subGroupTwoTextView.text = intent.getStringExtra("subTwoDetails")

        if(mainBackground != -1){
            mainLayout.background = getDrawable(mainBackground)
        }

    }
}