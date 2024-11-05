package com.etu.sae_501

import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.provider.MediaStore
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.material3.FilledTonalButton

class MainActivity : ComponentActivity() {
    // Enregistrement du résultat pour la capture d'image
    private val getPhotoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            // Traiter l'image ici...
        }
    }

    // Enregistrement du résultat pour le choix de la photo
    private val pickPhotoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            // Traiter l'URI de l'image ici...
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyScreen(
                onTakePhotoClick = {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    try {
                        getPhotoResult.launch(takePictureIntent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(this, "Erreur lors du démarrage de la caméra : " + e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                onChoosePhotoClick = {
                    val choosePhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    try {
                        pickPhotoResult.launch(choosePhotoIntent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(this, "Erreur lors du choix de la photo : " + e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
fun takePhotoButton(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Prendre une photo")
    }
}

@Composable
fun choosePhotoButton(onClick: () -> Unit) {
    FilledTonalButton(onClick = { onClick() }) {
        Text("Choisir une photo")
    }
}

@Composable
fun MyScreen(onTakePhotoClick: () -> Unit, onChoosePhotoClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        takePhotoButton(onClick = onTakePhotoClick)
        choosePhotoButton(onClick = onChoosePhotoClick)
    }
}
