package com.pira.ccloud.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pira.ccloud.R
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.navigation.AppScreens
import com.pira.ccloud.utils.StorageUtils

@Composable
fun FavoritesScreen(navController: NavController) {
    var favorites by remember { mutableStateOf<List<FavoriteItem>>(emptyList()) }
    val context = LocalContext.current
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    
    // Load favorites when screen is displayed
    LaunchedEffect(Unit) {
        favorites = StorageUtils.loadAllFavorites(context)
    }
    
    // Confirmation dialog for deleting all favorites
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("Delete All Favorites") },
            text = { Text("Are you sure you want to delete all favorites? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        StorageUtils.clearAllFavorites(context)
                        favorites = emptyList()
                        showDeleteAllDialog = false
                        // Show toast
                        android.widget.Toast.makeText(context, "All favorites deleted", android.widget.Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAllDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header without back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.favorites),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )
            
            // Delete all button (only show if there are favorites)
            if (favorites.isNotEmpty()) {
                IconButton(
                    onClick = { showDeleteAllDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete All"
                    )
                }
            }
        }
        
        // Content
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(text = "No favorites yet")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                items(favorites) { favorite ->
                    FavoriteItemCard(
                        favorite = favorite,
                        onClick = {
                            // Save the favorite to the appropriate database before navigating
                            StorageUtils.saveFavoriteToDatabase(context, favorite)
                            
                            // Navigate to the appropriate screen based on type
                            when (favorite.type) {
                                "movie" -> {
                                    navController.navigate("${AppScreens.SingleMovie.route.replace("{movieId}", favorite.id.toString())}")
                                }
                                "series" -> {
                                    navController.navigate("${AppScreens.SingleSeries.route.replace("{seriesId}", favorite.id.toString())}")
                                }
                            }
                        },
                        onDelete = {
                            StorageUtils.removeFavorite(context, favorite.id, favorite.type)
                            // Refresh the favorites list
                            favorites = StorageUtils.loadAllFavorites(context)
                            // Show toast
                            android.widget.Toast.makeText(context, "Removed from favorites", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    favorite: FavoriteItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster image
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(favorite.image)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = favorite.title,
                modifier = Modifier
                    .height(80.dp)
                    .width(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Title and details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = favorite.title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                // Show type and year
                Text(
                    text = "${favorite.type.capitalize()} • ${favorite.year}",
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp),
                        tint = androidx.compose.ui.graphics.Color.Red
                    )
                    Text(
                        text = String.format("%.1f", favorite.imdb)
                    )
                }
            }
            
            // Delete button for individual item
            IconButton(
                onClick = onDelete,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}