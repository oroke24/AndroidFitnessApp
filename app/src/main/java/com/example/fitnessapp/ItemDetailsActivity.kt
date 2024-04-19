package com.example.fitnessapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity



class ItemDetailsActivity() : ComponentActivity() {
    private lateinit var mainLayout: LinearLayout
    private lateinit var backButton : ImageButton
    private lateinit var titleEditText: EditText
    private lateinit var subGroupOneTitleTextView: TextView
    private lateinit var subGroupOneEditText: EditText
    private lateinit var subGroupTwoTitleTextView: TextView
    private lateinit var subGroupTwoEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var replaceButton: Button
    private lateinit var recipeDataManager: RecipeDataManager
    private lateinit var exerciseDataManager: ExerciseDataManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        mainLayout = findViewById(R.id.mainLayout)
        backButton = findViewById(R.id.backButton)
        titleEditText = findViewById(R.id.title)
        subGroupOneTitleTextView = findViewById(R.id.subGroupOneTitle)
        subGroupOneEditText = findViewById(R.id.subGroupOne)
        subGroupTwoTitleTextView = findViewById(R.id.subGroupTwoTitle)
        subGroupTwoEditText = findViewById(R.id.subGroupTwo)
        saveButton = findViewById(R.id.saveButton)
        replaceButton = findViewById(R.id.replaceButton)


        val email = intent.getStringExtra("email")?:""
        recipeDataManager = RecipeDataManager(email)
        exerciseDataManager = ExerciseDataManager(email)

        val fx = InteractionEffects()

        backButton.setOnClickListener{
            fx.imageButtonClickEffect(backButton)
            finish()
        }

        val dataManagerType = intent.getStringExtra("dataManagerType")

        val mainBackground = intent.getIntExtra("backgroundID", -1)
        val originalCard = intent.getStringExtra("title")?:""
        titleEditText.setText(originalCard)

        subGroupOneTitleTextView.text = intent.getStringExtra("subOneTitle")
        subGroupOneEditText.setText(intent.getStringExtra("subOneDetails"))

        subGroupTwoTitleTextView.text = intent.getStringExtra("subTwoTitle")
        subGroupTwoEditText.setText(intent.getStringExtra("subTwoDetails"))

        if(mainBackground != -1){
            mainLayout.background = getDrawable(mainBackground)
        }
        val textWatcher = object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(titleEditText.text.toString() == originalCard) {
                    replaceButton.visibility = View.VISIBLE
                }else replaceButton.visibility = View.GONE
                saveButton.visibility = View.VISIBLE
            }
        }
        titleEditText.addTextChangedListener(textWatcher)
        subGroupOneEditText.addTextChangedListener(textWatcher)
        subGroupTwoEditText.addTextChangedListener(textWatcher)

        saveButton.setOnClickListener {
            fx.buttonClickEffect(saveButton)
            var newCard = titleEditText.text.toString()
            if(newCard == originalCard) {
                newCard = "$originalCard+"
            }
            if(dataManagerType == "recipes"){
                val newRecipe = Recipe("", newCard, subGroupOneEditText.text.toString(), subGroupTwoEditText.text.toString())
                recipeDataManager.addRecipe(newRecipe)
            }

            if(dataManagerType == "exercises"){
                val newExercise = Exercise("", newCard, subGroupOneEditText.text.toString(), subGroupTwoEditText.text.toString())
                exerciseDataManager.addExercise(newExercise)
            }

            saveButton.visibility= View.GONE
            replaceButton.visibility= View.GONE
            finish()
        }

        replaceButton.setOnClickListener {
            fx.buttonClickEffect(replaceButton)

            if(dataManagerType == "recipes"){
                val newRecipe = Recipe("", titleEditText.text.toString(), subGroupOneEditText.text.toString(), subGroupTwoEditText.text.toString())
                recipeDataManager.addRecipe(newRecipe)
                recipeDataManager.deleteRecipe(originalCard)
            }

            if(dataManagerType == "exercises"){
                val newExercise = Exercise("", titleEditText.text.toString(), subGroupOneEditText.text.toString(), subGroupTwoEditText.text.toString())
                exerciseDataManager.addExercise(newExercise)
                exerciseDataManager.deleteExercise(originalCard)
            }

            saveButton.visibility= View.GONE
            replaceButton.visibility= View.GONE
            finish()
        }
    }
}