package com.etu.sae_501.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etu.sae_501.data.database.DatabaseProvider
import com.etu.sae_501.data.model.ScannedObject
import com.etu.sae_501.repository.ScannedObjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import kotlin.math.exp

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val model = try {
        val loadedModel = Module.load(assetFilePath(context, "Model_drink3.pt"))
        loadedModel
    } catch (e: Exception) {
        print(e.localizedMessage)
        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
        null
    }

    fun softmax(scores: FloatArray): FloatArray {
        val expScores = scores.map { exp(it.toDouble()) }
        val sumExpScores = expScores.sum()
        return expScores.map { (it / sumExpScores).toFloat() }.toFloatArray()
    }

    fun processImage(bitmap: Bitmap) {
        if (model == null) {
            Toast.makeText(context, "Modèle non chargé.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap, TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB)

            val outputTensor = model.forward(IValue.from(inputTensor)).toTensor()
            val scores = outputTensor.dataAsFloatArray

            val probabilities = softmax(scores)

            val labels = listOf("alcool", "eau", "soda")
            val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
            val predictedLabel = if (maxIndex >= 0) labels[maxIndex] else "Inconnu"
            val confidence = if (maxIndex >= 0) probabilities[maxIndex] * 100 else 0.0f

            // Ajouter l'objet scanné à l'historique
            val scannedObject = ScannedObject(
                name = predictedLabel,
                confidence = confidence,
                timestamp = System.currentTimeMillis()
            )

            // Insérer l'objet dans la base de données
            val repository = ScannedObjectRepository(DatabaseProvider.getDatabase(context).scannedObjectDao())
            CoroutineScope(Dispatchers.IO).launch {
                repository.insertObject(scannedObject)
            }

            Toast.makeText(context, "Objet détecté : $predictedLabel avec une confiance de ${"%.2f".format(confidence)}%", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur lors du traitement de l'image : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
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
