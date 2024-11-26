package com.etu.sae_501.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val model = try {
        Module.load(assetFilePath(context, "signatrix_efficientdet_coco.pt"))
    } catch (e: Exception) {
        Toast.makeText(context, "Erreur de chargement du modèle : ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        null
    }

    fun preprocessImage(bitmap: Bitmap): FloatArray {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val floatArray = FloatArray(3 * 224 * 224)
        var index = 0
        for (y in 0 until resizedBitmap.height) {
            for (x in 0 until resizedBitmap.width) {
                val pixel = resizedBitmap.getPixel(x, y)
                floatArray[index++] = (Color.red(pixel) / 255.0f - 0.485f) / 0.229f
                floatArray[index++] = (Color.green(pixel) / 255.0f - 0.456f) / 0.224f
                floatArray[index++] = (Color.blue(pixel) / 255.0f - 0.406f) / 0.225f
            }
        }
        return floatArray
    }

    fun processImage(bitmap: Bitmap) {
        if (model == null) {
            Toast.makeText(context, "Modèle non chargé.", Toast.LENGTH_SHORT).show()
            return
        }

        val inputTensor = Tensor.fromBlob(preprocessImage(bitmap), longArrayOf(1, 3, 224, 224))
        val outputTensor = model.forward(IValue.from(inputTensor)).toTensor()
        val scores = outputTensor.dataAsFloatArray

        val labels = listOf("soda", "eau", "alcool")
        val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: -1
        val predictedLabel = if (maxIndex >= 0) labels[maxIndex] else "Inconnu"

        Toast.makeText(context, "Objet détecté : $predictedLabel", Toast.LENGTH_SHORT).show()
    }

    val getPhotoResult = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                processImage(imageBitmap)
            } else {
                Toast.makeText(context, "Erreur : aucune image capturée.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val pickPhotoResult = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                processImage(bitmap)
            } else {
                Toast.makeText(context, "Erreur : aucune image sélectionnée.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // UI de la page
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TitleText()

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = {
                        val choosePhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        try {
                            pickPhotoResult.launch(choosePhotoIntent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "Erreur lors du choix de la photo : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Choisir une photo")
                }

                Button(
                    onClick = {
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        try {
                            getPhotoResult.launch(takePictureIntent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "Erreur lors du démarrage de la caméra : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Prendre une photo")
                }
            }
        }
    }
}

@Composable
fun TitleText() {
    Text(
        "Capturez une image ou importez-en une pour identifier des objets en temps réel",
        fontSize = 22.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .widthIn(max = 300.dp)
    )
}

fun assetFilePath(context: Context, assetName: String): String {
    val file = File(context.filesDir, assetName)
    if (!file.exists()) {
        context.assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
    return file.absolutePath
}