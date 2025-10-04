package com.pira.ccloud.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pira.ccloud.screens.MoviesScreen
import com.pira.ccloud.screens.SearchScreen
import com.pira.ccloud.screens.SeriesScreen
import com.pira.ccloud.screens.SettingsScreen
import com.pira.ccloud.screens.SingleMovieScreen
import com.pira.ccloud.ui.theme.ThemeSettings

@Composable
fun AppNavigation(
    navController: NavHostController,
    onThemeSettingsChanged: (ThemeSettings) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.Movies.route
    ) {
        composable(route = AppScreens.Movies.route) {
            MoviesScreen(navController = navController)
        }
        composable(route = AppScreens.Series.route) {
            SeriesScreen()
        }
        composable(route = AppScreens.Search.route) {
            SearchScreen()
        }
        composable(route = AppScreens.Settings.route) {
            SettingsScreen(onThemeSettingsChanged)
        }
        composable(
            route = AppScreens.SingleMovie.route,
            arguments = listOf(navArgument("movieId") { defaultValue = "0" })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
            SingleMovieScreen(movieId = movieId, navController = navController)
        }
    }
}