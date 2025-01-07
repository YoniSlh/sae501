import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Divider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale


data class HistoryItem(
    val imagePath: String?,
    val title: String,
    val subtitle: String,
    val progress: Int,
    val onClick: () -> Unit,
    val onDelete: () -> Unit
)

@Composable
fun DetailScreen(
    title: String,
    date: String,
    confidence: Float,
    historyItems: List<HistoryItem>,
    imagePath: String?,
    isFavorite: Boolean, // Indique si l'objet est favori
    onBackClick: () -> Unit,
    onBookmarkClick: (Boolean) -> Unit // Action à exécuter quand favoris change
) {
    val favoriteState = remember { mutableStateOf(isFavorite) } // État local

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }

            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            IconButton(onClick = {
                favoriteState.value = !favoriteState.value // Inverse l'état local
                onBookmarkClick(favoriteState.value) // Informe le parent du changement
            }) {
                Icon(
                    imageVector = if (favoriteState.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favoris",
                    tint = if (favoriteState.value) Color.Red else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
        ) {
            imagePath?.let {
                val bitmap = BitmapFactory.decodeFile(it)
                bitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Image de l'objet",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } ?: Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Image de l'objet",
                modifier = Modifier.fillMaxSize(),
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(date, style = MaterialTheme.typography.titleMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        Divider(color = Color.Gray, thickness = 0.8.dp)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Confiance", style = MaterialTheme.typography.titleLarge)
        Text(confidence.toInt().toString() + " %", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(historyItems) { item ->
                HistoryItem(
                    imagePath = item.imagePath,
                    title = item.title,
                    subtitle = item.subtitle,
                    progress = item.progress,
                    onClick = item.onClick,
                    onDelete = item.onDelete
                )
            }
        }
    }
}



