package com.pira.ccloud.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pira.ccloud.R
import com.pira.ccloud.data.model.SubtitleSettings
import com.pira.ccloud.ui.theme.ThemeMode
import com.pira.ccloud.ui.theme.ThemeSettings
import com.pira.ccloud.ui.theme.ThemeManager
import com.pira.ccloud.ui.theme.colorOptions
import com.pira.ccloud.ui.theme.defaultPrimaryColor
import com.pira.ccloud.ui.theme.defaultSecondaryColor
import com.pira.ccloud.utils.StorageUtils
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Brightness1
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettingsScreen(onThemeSettingsChanged: (ThemeSettings) -> Unit = {}) {
    val themeManager = ThemeManager(androidx.compose.ui.platform.LocalContext.current)
    var themeSettings by remember { mutableStateOf(themeManager.loadThemeSettings()) }
    val context = LocalContext.current
    var subtitleSettings by remember { mutableStateOf(StorageUtils.loadSubtitleSettings(context)) }
    
    // Update parent when settings change
    fun updateThemeSettings(newSettings: ThemeSettings) {
        themeSettings = newSettings
        onThemeSettingsChanged(newSettings)
        themeManager.saveThemeSettings(newSettings)
    }
    
    // Update subtitle settings
    fun updateSubtitleSettings(newSettings: SubtitleSettings) {
        subtitleSettings = newSettings
        StorageUtils.saveSubtitleSettings(context, newSettings)
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(animationSpec = tween(300))
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
        
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(400)) + slideInVertically(animationSpec = tween(400, delayMillis = 100)),
                exit = fadeOut(animationSpec = tween(400)) + slideOutVertically(animationSpec = tween(400))
            ) {
                ThemeModeSection(
                    currentThemeMode = themeSettings.themeMode,
                    onThemeModeSelected = { mode ->
                        val newSettings = themeSettings.copy(themeMode = mode)
                        updateThemeSettings(newSettings)
                    }
                )
            }
        }
        
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500, delayMillis = 200)),
                exit = fadeOut(animationSpec = tween(500)) + slideOutVertically(animationSpec = tween(500))
            ) {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(animationSpec = tween(600, delayMillis = 300)),
                exit = fadeOut(animationSpec = tween(600)) + slideOutVertically(animationSpec = tween(600))
            ) {
                ColorSelectionSection(
                    title = "Primary Color",
                    selectedColor = themeSettings.primaryColor,
                    onColorSelected = { color ->
                        val newSettings = themeSettings.copy(primaryColor = color)
                        updateThemeSettings(newSettings)
                    }
                )
            }
        }
        
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(700)) + slideInVertically(animationSpec = tween(700, delayMillis = 400)),
                exit = fadeOut(animationSpec = tween(700)) + slideOutVertically(animationSpec = tween(700))
            ) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(animationSpec = tween(800, delayMillis = 500)),
                exit = fadeOut(animationSpec = tween(800)) + slideOutVertically(animationSpec = tween(800))
            ) {
                ColorSelectionSection(
                    title = "Secondary Color",
                    selectedColor = themeSettings.secondaryColor,
                    onColorSelected = { color ->
                        val newSettings = themeSettings.copy(secondaryColor = color)
                        updateThemeSettings(newSettings)
                    }
                )
            }
        }
        
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(900)) + slideInVertically(animationSpec = tween(900, delayMillis = 600)),
                exit = fadeOut(animationSpec = tween(900)) + slideOutVertically(animationSpec = tween(900))
            ) {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(animationSpec = tween(1000, delayMillis = 700)),
                exit = fadeOut(animationSpec = tween(1000)) + slideOutVertically(animationSpec = tween(1000))
            ) {
                SubtitleSettingsSection(
                    subtitleSettings = subtitleSettings,
                    onSettingsChanged = { updateSubtitleSettings(it) }
                )
            }
        }
        
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1100)) + slideInVertically(animationSpec = tween(1100, delayMillis = 800)),
                exit = fadeOut(animationSpec = tween(1100)) + slideOutVertically(animationSpec = tween(1100))
            ) {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1200)) + slideInVertically(animationSpec = tween(1200, delayMillis = 900)),
                exit = fadeOut(animationSpec = tween(1200)) + slideOutVertically(animationSpec = tween(1200))
            ) {
                ResetToDefaultsButton(
                    onClick = {
                        val defaultSettings = ThemeSettings()
                        updateThemeSettings(defaultSettings)
                    }
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ThemeModeSection(
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Theme Mode",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            ThemeModeOption(
                mode = ThemeMode.LIGHT,
                label = "Light",
                isSelected = currentThemeMode == ThemeMode.LIGHT,
                onSelect = onThemeModeSelected
            )
            
            ThemeModeOption(
                mode = ThemeMode.DARK,
                label = "Dark",
                isSelected = currentThemeMode == ThemeMode.DARK,
                onSelect = onThemeModeSelected
            )
            
            ThemeModeOption(
                mode = ThemeMode.SYSTEM,
                label = "System Default",
                isSelected = currentThemeMode == ThemeMode.SYSTEM,
                onSelect = onThemeModeSelected
            )
        }
    }
}

@Composable
fun ThemeModeOption(
    mode: ThemeMode,
    label: String,
    isSelected: Boolean,
    onSelect: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(mode) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect(mode) }
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun ColorSelectionSection(
    title: String,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Display color options in rows of 4
            for (rowColors in colorOptions.chunked(4)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowColors.forEach { color ->
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            ColorOption(
                                color = color,
                                isSelected = selectedColor == color,
                                onSelect = onColorSelected
                            )
                        }
                    }
                    // Fill remaining spaces if less than 4 items
                    repeat(4 - rowColors.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            // Add default color option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                val defaultColor = if (title == "Primary Color") defaultPrimaryColor else defaultSecondaryColor
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    ColorOption(
                        color = defaultColor,
                        isSelected = selectedColor == defaultColor,
                        onSelect = onColorSelected,
                        label = "Default"
                    )
                }
            }
        }
    }
}

@Composable
fun ColorOption(
    color: Color,
    isSelected: Boolean,
    onSelect: (Color) -> Unit,
    label: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color)
                .clickable { onSelect(color) }
                .then(
                    if (isSelected) {
                        Modifier.padding(4.dp)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = if (color == Color.White || color == Color.Yellow) Color.Black else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ResetToDefaultsButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Reset to Defaults",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SubtitleSettingsSection(
    subtitleSettings: SubtitleSettings,
    onSettingsChanged: (SubtitleSettings) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Subtitle Settings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Background color setting
            SubtitleColorSetting(
                title = "Background Color",
                currentColor = Color(subtitleSettings.backgroundColor),
                onColorSelected = { color ->
                    onSettingsChanged(subtitleSettings.copy(backgroundColor = color.toArgb()))
                },
                noColorOption = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Text color setting
            SubtitleColorSetting(
                title = "Text Color",
                currentColor = Color(subtitleSettings.textColor),
                onColorSelected = { color ->
                    onSettingsChanged(subtitleSettings.copy(textColor = color.toArgb()))
                },
                defaultColor = Color.Yellow
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Border color setting
            SubtitleColorSetting(
                title = "Border Color",
                currentColor = Color(subtitleSettings.borderColor),
                onColorSelected = { color ->
                    onSettingsChanged(subtitleSettings.copy(borderColor = color.toArgb()))
                },
                noColorOption = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Text size setting
            Text(
                text = "Text Size: ${subtitleSettings.textSize.toInt()}sp",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Slider(
                value = subtitleSettings.textSize,
                onValueChange = { size ->
                    onSettingsChanged(subtitleSettings.copy(textSize = size))
                },
                valueRange = 10f..30f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SubtitleColorSetting(
    title: String,
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    noColorOption: Boolean = false,
    defaultColor: Color? = null
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Color options
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // No color option (transparent)
                if (noColorOption) {
                    ColorOptionButton(
                        color = Color.Transparent,
                        isSelected = currentColor == Color.Transparent,
                        onClick = { onColorSelected(Color.Transparent) },
                        showBorder = true
                    )
                }
                
                // Default color option
                if (defaultColor != null) {
                    ColorOptionButton(
                        color = defaultColor,
                        isSelected = currentColor == defaultColor,
                        onClick = { onColorSelected(defaultColor) }
                    )
                }
                
                // Standard color options
                listOf(Color.White, Color.Black, Color.Red, Color.Blue, Color.Green).forEach { color ->
                    ColorOptionButton(
                        color = color,
                        isSelected = currentColor == color,
                        onClick = { onColorSelected(color) }
                    )
                }
            }
            
            // Current color preview
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(currentColor)
                    .then(
                        if (currentColor == Color.Transparent) {
                            Modifier.background(Color.Gray.copy(alpha = 0.3f))
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

@Composable
fun ColorOptionButton(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    showBorder: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (color == Color.Transparent && showBorder) {
                    color
                } else {
                    color
                }
            )
            .then(
                if (isSelected) {
                    Modifier.background(
                        color = color,
                        shape = RoundedCornerShape(4.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (color == Color.White || color == Color.Yellow) Color.Black else Color.White,
                modifier = Modifier.size(16.dp)
            )
        } else if (color == Color.Transparent && showBorder) {
            Icon(
                imageVector = Icons.Default.Brightness1,
                contentDescription = "No color",
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}