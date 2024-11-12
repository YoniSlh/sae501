package com.etu.sae_501.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val listOfNavItems = listOf(
    NavItem(
        label = "Accueil",
        icon = Icons.Default.Home,
        route = Screens.HomeScreen.name
    ),
    NavItem(
        label = "Historique",
        icon = Icons.Default.DateRange,
        route = Screens.HistoryScreen.name
    ),
    NavItem(
        label = "Sauvegardés",
        icon = Icons.Default.Star,
        route = Screens.SavedScreen.name

    )
)