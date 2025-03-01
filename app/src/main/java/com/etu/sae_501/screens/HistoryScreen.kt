package com.etu.sae_501.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etu.sae_501.data.model.ScannedObject
import com.etu.sae_501.data.database.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.BitmapFactory
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavController
import com.etu.sae_501.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Accès à la base de données et récupération des données
    val database = DatabaseProvider.getDatabase(context)
    val scannedObjectDao = database.scannedObjectDao()

    // Liste complète des objets scannés
    var historyItems by remember { mutableStateOf<List<ScannedObject>>(emptyList()) }

    // Filtre sélectionné
    var selectedFilter by remember { mutableStateOf<List<String>>(emptyList()) }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    // Récupérer les objets scannés depuis la base de données
    LaunchedEffect(true) {
        coroutineScope.launch {
            scannedObjectDao.getAllObjects().collect { objects ->
                historyItems = objects.sortedByDescending { it.timestamp } // Trier par date
            }
        }
    }

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
        ) {
            // Barre de filtres
            FilterBar(
                selectedFilter = selectedFilter,
                showFavoritesOnly = showFavoritesOnly,
                onFilterChange = { newFilter ->
                    selectedFilter = newFilter
                },
                onFavoritesToggle = { showFavorites ->
                    showFavoritesOnly = showFavorites
                }
            )

            // Appliquer les filtres
            val filteredItems = historyItems.filter { item ->
                val matchesType = if (selectedFilter.isEmpty()) true else selectedFilter.any { filter ->
                    item.name.contains(filter, ignoreCase = true)
                }
                val matchesFavorites = if (showFavoritesOnly) item.isFavorite else true
                matchesType && matchesFavorites
            }

            if (filteredItems.isEmpty()) {
                Text(
                    text = "Aucun élément trouvé.",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems) { item ->
                        HistoriqueItem(
                            imagePath = item.imagePath,
                            title = item.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                            subtitle = "Date de scan: ${
                                java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(item.timestamp))
                            }",
                            progress = item.confidence.toInt(),
                            isFavorite = item.isFavorite,
                            onClick = {
                                navController.navigate("${Screens.DetailScreen.name}/${item.id}")
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        scannedObjectDao.deleteObject(item)
                                    }
                                }
                            },
                            onFavoriteToggle = {
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        item.isFavorite = !item.isFavorite
                                        scannedObjectDao.updateObject(item)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterBar(
    selectedFilter: List<String>,
    showFavoritesOnly: Boolean,
    onFilterChange: (List<String>) -> Unit,
    onFavoritesToggle: (Boolean) -> Unit
) {
    val filters = listOf("Alcool", "Soda", "Eau") // Liste des filtres disponibles
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter.contains(filter),
                onClick = {
                    val newFilter = if (selectedFilter.contains(filter)) {
                        selectedFilter - filter // Supprimer le filtre si déjà sélectionné
                    } else {
                        selectedFilter + filter // Ajouter le filtre sinon
                    }
                    onFilterChange(newFilter)
                },
                label = { Text(filter) }
            )
        }
        FilterChip(
            selected = showFavoritesOnly,
            onClick = { onFavoritesToggle(!showFavoritesOnly) },
            label = { Text("Favoris") }
        )
    }
}

@Composable
fun HistoriqueItem(
    imagePath: String?,
    title: String,
    subtitle: String,
    progress: Int,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val progressColor = when {
        progress < 20 -> Color.Red
        progress < 70 -> Color(0xFFFFCC02)
        else -> Color(0xFF35C759)
    }

    var expanded by remember { mutableStateOf(false) }

    var isCurrentlyFavorited by remember { mutableStateOf(isFavorite) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Afficher l'image si elle est disponible, sinon un placeholder
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray, shape = CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("N/A", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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

                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = subtitle, fontSize = 14.sp, color = Color.Gray)
        }

        IconButton(onClick = {
            isCurrentlyFavorited = !isCurrentlyFavorited
            onFavoriteToggle()
        }) {
            Icon(
                imageVector = if (isCurrentlyFavorited) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = null,
                tint = if (isCurrentlyFavorited) Color.Red else Color.Gray
            )
        }

        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Options")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = (-256).dp, y = 0.dp)
        ) {
            DropdownMenuItem(
                text = { Text("Supprimer") },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}