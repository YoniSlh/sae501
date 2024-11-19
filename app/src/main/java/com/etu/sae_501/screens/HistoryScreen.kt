package com.etu.sae_501.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.unit.DpOffset

data class HistoryItemData(
    val iconRes: Int,
    val title: String,
    val subtitle: String,
    val progress: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    // Obtenir le contexte pour le Toast
    val context = LocalContext.current

    val items = listOf(
        HistoryItemData(
            iconRes = android.R.drawable.ic_menu_gallery,
            title = "Item 1",
            subtitle = "Description for item 1.",
            progress = 54
        ),
        HistoryItemData(
            iconRes = android.R.drawable.ic_menu_gallery,
            title = "Item 2",
            subtitle = "Description for item 2.",
            progress = 11
        ),
        HistoryItemData(
            iconRes = android.R.drawable.ic_menu_gallery,
            title = "Item 3",
            subtitle = "Description for item 3.",
            progress = 92
        ),
        HistoryItemData(
            iconRes = android.R.drawable.ic_menu_gallery,
            title = "Item 4",
            subtitle = "Description for item 4.",
            progress = 54
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Historique", fontSize = 24.sp, fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(bottom = 42.dp)
        ) {
            items.forEach { item ->
                HistoriqueItem(
                    iconRes = item.iconRes,
                    title = item.title,
                    subtitle = item.subtitle,
                    progress = item.progress,
                    onClick = {
                        // Afficher un Toast lors du clic
                        Toast.makeText(context, "Clicked: ${item.title}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun HistoriqueItem(
    iconRes: Int,
    title: String,
    subtitle: String,
    progress: Int,
    onClick: () -> Unit
) {
    val progressColor = when {
        progress < 20 -> Color.Red
        progress < 70 -> Color(0xFFFFCC02)
        else -> Color(0xFF35C759)
    }

    // State pour afficher ou cacher le menu
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icône
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Textes
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Badge pourcentage
                Box(
                    modifier = Modifier
                        .background(color = progressColor, shape = CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$progress %",
                        color = if (progressColor == Color(0xFFFFCC02) || progressColor == Color(0xFF35C759)) Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Titre
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sous-titre
            Text(text = subtitle, fontSize = 14.sp, color = Color.Gray)
        }

        // Menu contextuel (bouton 3 points)
        IconButton(onClick = { expanded = true }) { // Ouvrir le menu
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu"
            )
        }

        // DropdownMenu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = (-256).dp, y = 0.dp)
        ) {
            DropdownMenuItem(
                text = { Text("Ajouter aux favoris") },
                onClick = {
                    expanded = false // Close menu after clicking
                    // Action for adding to favorites
                }
            )
            DropdownMenuItem(
                text = { Text("Supprimer") },
                onClick = {
                    expanded = false // Close menu after clicking
                    // Action for deleting the item
                }
            )
        }
    }
}