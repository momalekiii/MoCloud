package com.pira.ccloud.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.pira.ccloud.data.model.FavoriteGroup
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.navigation.AppScreens
import com.pira.ccloud.utils.StorageUtils
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    var favorites by remember { mutableStateOf<List<FavoriteItem>>(emptyList()) }
    var groups by remember { mutableStateOf<List<FavoriteGroup>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf<FavoriteGroup?>(null) }
    val context = LocalContext.current
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showMoveToGroupDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<FavoriteItem?>(null) }
    
    // Load favorites and groups when screen is displayed
    LaunchedEffect(Unit) {
        favorites = StorageUtils.loadAllFavorites(context)
        groups = StorageUtils.loadAllFavoriteGroups(context)
        selectedGroup = groups.firstOrNull { it.isDefault } ?: groups.firstOrNull()
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
    
    // Dialog for creating a new group
    if (showCreateGroupDialog) {
        var groupName by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showCreateGroupDialog = false },
            title = { Text("Create New Playlist") },
            text = {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Playlist Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (groupName.isNotBlank()) {
                            val newGroup = FavoriteGroup(
                                id = UUID.randomUUID().toString(),
                                name = groupName,
                                isDefault = false
                            )
                            StorageUtils.saveFavoriteGroup(context, newGroup)
                            groups = StorageUtils.loadAllFavoriteGroups(context)
                            showCreateGroupDialog = false
                            groupName = ""
                        }
                    },
                    enabled = groupName.isNotBlank()
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCreateGroupDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Dialog for moving item to groups (multi-select)
    if (showMoveToGroupDialog && selectedItem != null) {
        val item = selectedItem!!
        var selectedGroups by remember { mutableStateOf<List<String>>(emptyList()) }
        
        // Initialize selected groups
        LaunchedEffect(item) {
            val currentGroups = StorageUtils.getGroupsForFavorite(context, item.id, item.type)
            selectedGroups = currentGroups.map { it.id }
        }
        
        AlertDialog(
            onDismissRequest = { showMoveToGroupDialog = false },
            title = { Text("Select Playlists") },
            text = {
                LazyColumn {
                    items(groups.filter { !it.isDefault }) { group ->
                        val isChecked = selectedGroups.contains(group.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedGroups = if (isChecked) {
                                        selectedGroups.filter { it != group.id }
                                    } else {
                                        selectedGroups + group.id
                                    }
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    selectedGroups = if (checked) {
                                        selectedGroups + group.id
                                    } else {
                                        selectedGroups.filter { it != group.id }
                                    }
                                }
                            )
                            Text(
                                text = group.name,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Update group memberships
                        groups.filter { !it.isDefault }.forEach { group ->
                            if (selectedGroups.contains(group.id)) {
                                // Add to group if not already there
                                if (!StorageUtils.isFavoriteInGroup(context, group.id, item.id, item.type)) {
                                    StorageUtils.addFavoriteToGroup(context, group.id, item.id, item.type)
                                }
                            } else {
                                // Remove from group if it was there
                                if (StorageUtils.isFavoriteInGroup(context, group.id, item.id, item.type)) {
                                    StorageUtils.removeFavoriteFromGroup(context, group.id, item.id, item.type)
                                }
                            }
                        }
                        
                        // Refresh data
                        groups = StorageUtils.loadAllFavoriteGroups(context)
                        favorites = if (selectedGroup != null && !selectedGroup!!.isDefault) {
                            StorageUtils.getFavoritesInGroup(context, selectedGroup!!.id)
                        } else {
                            StorageUtils.loadAllFavorites(context)
                        }
                        
                        showMoveToGroupDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showMoveToGroupDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = selectedGroup?.name ?: stringResource(R.string.favorites),
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            actions = {
                // Create group button
                IconButton(
                    onClick = { showCreateGroupDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Playlist"
                    )
                }
                
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
        )
        
        // Group selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Playlists:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Display groups as selectable chips
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                items(groups) { group ->
                    Card(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedGroup = group
                                // Load favorites for this group
                                favorites = if (group.isDefault) {
                                    StorageUtils.loadAllFavorites(context)
                                } else {
                                    StorageUtils.getFavoritesInGroup(context, group.id)
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (selectedGroup?.id == group.id) 4.dp else 0.dp),
                        colors = if (selectedGroup?.id == group.id) {
                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        } else {
                            CardDefaults.cardColors()
                        }
                    ) {
                        Text(
                            text = group.name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
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
                    if (selectedGroup?.isDefault == false) {
                        Text(
                            text = "Add items to this playlist by moving them from the default Favorites playlist",
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
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
                            favorites = if (selectedGroup != null && !selectedGroup!!.isDefault) {
                                StorageUtils.getFavoritesInGroup(context, selectedGroup!!.id)
                            } else {
                                StorageUtils.loadAllFavorites(context)
                            }
                            // Show toast
                            android.widget.Toast.makeText(context, "Removed from favorites", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        onMoveToGroup = { item ->
                            selectedItem = item
                            showMoveToGroupDialog = true
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
    onDelete: () -> Unit,
    onMoveToGroup: (FavoriteItem) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
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
            
            // Menu button for additional actions
            Box {
                IconButton(
                    onClick = { showMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Move to Playlist") },
                        onClick = {
                            onMoveToGroup(favorite)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            onDelete()
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}