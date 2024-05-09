package com.example.fitnessapp

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ItemDetailsActivity() : ComponentActivity() {
    private lateinit var mainLayout: LinearLayout
    private lateinit var backButton : ImageButton
    private lateinit var titleEditText: EditText
    private lateinit var itemPhoto: ImageView
    private lateinit var itemPhotoUri: Uri
    private lateinit var subGroupOneTitleTextView: TextView
    private lateinit var subGroupOneEditText: EditText
    private lateinit var subGroupTwoTitleTextView: TextView
    private lateinit var subGroupTwoEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var replaceButton: Button
    private lateinit var recipeDataManager: RecipeDataManager
    private lateinit var exerciseDataManager: ExerciseDataManager
    private lateinit var imageDataManager: ImageDataManager
    val fx = InteractionEffects()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        mainLayout = findViewById(R.id.mainLayout)
        backButton = findViewById(R.id.backButton)
        titleEditText = findViewById(R.id.title)
        itemPhoto = findViewById(R.id.itemPhoto)
        subGroupOneTitleTextView = findViewById(R.id.subGroupOneTitle)
        subGroupOneEditText = findViewById(R.id.subGroupOne)
        subGroupTwoTitleTextView = findViewById(R.id.subGroupTwoTitle)
        subGroupTwoEditText = findViewById(R.id.subGroupTwo)
        saveButton = findViewById(R.id.saveButton)
        replaceButton = findViewById(R.id.replaceButton)

        val email = intent.getStringExtra("email")?:""
        recipeDataManager = RecipeDataManager(email)
        exerciseDataManager = ExerciseDataManager(email)
        imageDataManager = ImageDataManager(email)

        backButton.setOnClickListener{
            fx.imageButtonClickEffect(backButton)
            finish()
        }
        val dataManagerType = intent.getStringExtra("dataManagerType")
        val originalCard = intent.getStringExtra("title")?:""
        titleEditText.setText(originalCard)
        imageDataManager.downloadRecipePhoto(titleEditText.text.toString())?.addOnSuccessListener {uri->
            //itemPhoto.setImageURI(uri)
            Glide.with(this)
                .load(uri)
                .into(itemPhoto)
        }?.addOnFailureListener { exception ->
            Log.e(TAG, "Error downloading recipe photo", exception)
        }
        subGroupOneTitleTextView.text = intent.getStringExtra("subOneTitle")
        subGroupOneEditText.setText(intent.getStringExtra("subOneDetails"))
        subGroupTwoTitleTextView.text = intent.getStringExtra("subTwoTitle")
        subGroupTwoEditText.setText(intent.getStringExtra("subTwoDetails"))
        when(dataManagerType){
            "recipes" -> mainLayout.background = getDrawable(R.drawable.cool_background)
            "exercises" -> {mainLayout.background = getDrawable(R.drawable.cool_background2)
                            itemPhoto.visibility = View.GONE}
            else -> mainLayout.background = getDrawable(R.drawable.background_grey_outline)
        }
        itemPhoto.setOnLongClickListener{
            //Todo: set up selection dialogue from device
            selectImageFromGallery()
            true
        }

        val textWatcher = object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                replaceButton.visibility = View.VISIBLE
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
                imageDataManager.uploadRecipePhoto(itemPhotoUri, titleEditText.text.toString())
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
                imageDataManager.uploadRecipePhoto(itemPhotoUri, titleEditText.text.toString())
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
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val selectedImageUri: Uri? = data.data
                if (selectedImageUri != null) {
                    itemPhoto.setImageURI(selectedImageUri)
                    itemPhotoUri = selectedImageUri
                    saveButton.visibility = View.VISIBLE
                    replaceButton.visibility = View.VISIBLE
                    /*
                    val storageRef = FirebaseStorage.getInstance().reference
                    val imageRef = storageRef.child("images/${selectedImageUri.lastPathSegment}")
                    imageRef.putFile(selectedImageUri)
                     */
                }
            }
        }
    }
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }
}