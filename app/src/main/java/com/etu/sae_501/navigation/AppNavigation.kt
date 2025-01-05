package com.etu.sae_501.navigation

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.etu.sae_501.screens.SavedScreen
import com.etu.sae_501.screens.HistoryScreen
import com.etu.sae_501.screens.HomeScreen
import com.etu.sae_501.viewmodel.HistoryViewModel
import com.etu.sae_501.repository.ScannedObjectRepository
import com.etu.sae_501.data.database.DatabaseProvider

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Récupère le Dao via DatabaseProvider
    val context = LocalContext.current
    val dao = remember { DatabaseProvider.getDatabase(context).scannedObjectDao() }
    val repository = remember { ScannedObjectRepository(dao) }
    val historyViewModel = remember { HistoryViewModel(repository) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                listOfNavItems.forEach { navItem ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                        onClick = {
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.label
                            )
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.HomeScreen.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screens.HomeScreen.name) {
                HomeScreen()
            }
            composable(Screens.HistoryScreen.name) {
                HistoryScreen()
            }
            composable(Screens.SavedScreen.name) {
                SavedScreen()
            }
        }
    }
}
