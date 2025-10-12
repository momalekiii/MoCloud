package com.pira.ccloud

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.FrameLayout
import kotlin.math.abs
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Forward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import com.pira.ccloud.data.model.SubtitleSettings
import com.pira.ccloud.data.model.VideoPlayerSettings
import com.pira.ccloud.data.model.FontSettings
import com.pira.ccloud.utils.StorageUtils
import com.pira.ccloud.ui.theme.FontManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Extension function to set subtitle text size on PlayerView
fun PlayerView.setSubtitleTextSize(spSize: Float) {
    // Convert sp to pixels
    val displayMetrics = context.resources.displayMetrics
    val pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spSize, displayMetrics)
    
    // Set the subtitle text size
    subtitleView?.setFixedTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, pixels)
}

// Extension function to set subtitle colors and font
fun PlayerView.setSubtitleColors(settings: SubtitleSettings, typeface: Typeface? = null) {
    // Create a custom CaptionStyleCompat with the typeface
    val style = CaptionStyleCompat(
        settings.textColor,
        settings.backgroundColor,
        settings.borderColor,
        CaptionStyleCompat.EDGE_TYPE_OUTLINE,
        settings.borderColor,
        typeface
    )
    subtitleView?.setStyle(style)
    
    // Note: ExoPlayer's subtitle rendering has limited support for custom fonts.
    // The font may not be applied to all subtitle formats or on all Android versions.
    // This is a known limitation of ExoPlayer's subtitle rendering system.
}

class VideoPlayerActivity : ComponentActivity() {
    companion object {
        const val EXTRA_VIDEO_URL = "video_url"
        const val REQUEST_WRITE_SETTINGS = 1001
        
        fun start(context: Context, videoUrl: String) {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_URL, videoUrl)
            }
            context.startActivity(intent)
        }
    }
    
    private var exoPlayer: ExoPlayer? = null
    private var videoUrl: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set fullscreen landscape mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        // Enable immersive full-screen mode
        enableFullScreenMode()
        
        // Keep screen on while in video player
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Check and request brightness control permission if needed
        checkAndRequestBrightnessPermission()
        
        videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL)
        
        if (videoUrl != null) {
            setContent {
                VideoPlayerScreen(videoUrl!!, this::finish) { player ->
                    exoPlayer = player
                }
            }
        } else {
            finish()
        }
    }
    
    private fun checkAndRequestBrightnessPermission() {
        // Check if we have permission to write system settings
        if (!Settings.System.canWrite(this)) {
            // Request permission to write system settings
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:${packageName}")
            startActivityForResult(intent, REQUEST_WRITE_SETTINGS)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_WRITE_SETTINGS) {
            // Permission result for writing system settings
            if (Settings.System.canWrite(this)) {
                // Permission granted
            } else {
                // Permission denied - we'll work with window-level brightness only
            }
        }
    }
    
    // Handle TV remote control key events
    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        exoPlayer?.let { player ->
            when (keyCode) {
                android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                android.view.KeyEvent.KEYCODE_DPAD_CENTER -> {
                    player.playWhenReady = !player.playWhenReady
                    return true
                }
                android.view.KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    player.playWhenReady = true
                    return true
                }
                android.view.KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    player.playWhenReady = false
                    return true
                }
                android.view.KeyEvent.KEYCODE_DPAD_LEFT -> {
                    val newPosition = (player.currentPosition - 10000).coerceAtLeast(0L) // Rewind 10 seconds
                    player.seekTo(newPosition)
                    return true
                }
                android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    val newPosition = (player.currentPosition + 10000).coerceAtMost(player.duration) // Forward 10 seconds
                    player.seekTo(newPosition)
                    return true
                }
                android.view.KeyEvent.KEYCODE_BACK -> {
                    finish()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
    
    private fun enableFullScreenMode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // For Android 11 and above
                window.insetsController?.let { controller ->
                    controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // For Android 4.4 to Android 10
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
            } else {
                // For even older versions
                @Suppress("DEPRECATION")
                window.addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        } catch (e: Exception) {
            // Fallback to basic fullscreen if there's an issue
            @Suppress("DEPRECATION")
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            exoPlayer?.release()
        } catch (e: Exception) {
            // Ignore any exceptions during release
        }
        exoPlayer = null
        
        // Remove keep screen on flag to conserve battery
        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    
    override fun onResume() {
        super.onResume()
        // Re-enable full-screen mode when resuming
        try {
            enableFullScreenMode()
        } catch (e: Exception) {
            // Ignore fullscreen errors
        }
    }
}

@Composable
fun VideoPlayerScreen(
    videoUrl: String,
    onBack: () -> Unit,
    onPlayerReady: (ExoPlayer) -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var showControls by remember { mutableStateOf(true) }
    var isSeeking by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf<String?>(null) }
    var showForwardIndicator by remember { mutableStateOf(false) }
    var showRewindIndicator by remember { mutableStateOf(false) }
    var wasPlayingBeforeSeek by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var showSpeedDropdown by remember { mutableStateOf(false) }
    
    // Brightness and volume control states
    var showBrightnessIndicator by remember { mutableStateOf(false) }
    var showVolumeIndicator by remember { mutableStateOf(false) }
    var brightnessLevel by remember { mutableStateOf(0f) }
    var volumeLevel by remember { mutableStateOf(0f) }
    
    // Predefined playback speed options
    val speedOptions = remember {
        listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.5f, 3.0f, 3.5f)
    }
    
    // Load font settings
    val fontSettings = remember(context) {
        StorageUtils.loadFontSettings(context)
    }
    
    // Load custom font typeface
    val customTypeface = remember(fontSettings.fontType) {
        when (fontSettings.fontType) {
            com.pira.ccloud.data.model.FontType.DEFAULT -> null
            com.pira.ccloud.data.model.FontType.VAZIRMATN -> {
                try {
                    // Load the Vazirmatn font from assets
                    Typeface.createFromAsset(context.assets, "font/vazirmatn_regular.ttf")
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
    
    // Load video player settings (without affecting playback speed)
    val videoPlayerSettings = remember(context) {
        StorageUtils.loadVideoPlayerSettings(context)
    }
    
    // Load subtitle settings
    val subtitleSettings = remember(context) {
        StorageUtils.loadSubtitleSettings(context)
    }
    
    val exoPlayer = remember(context) {
        try {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
                prepare()
                playWhenReady = true // Start playing by default
                // Set initial playback speed
                setPlaybackSpeed(playbackSpeed)
            }
        } catch (e: Exception) {
            playerError = "Failed to initialize player: ${e.message}"
            null
        }
    }
    
    // Notify activity of player reference
    LaunchedEffect(Unit) {
        try {
            exoPlayer?.let { onPlayerReady(it) }
        } catch (e: Exception) {
            // Ignore callback errors
        }
    }
    
    // Update player state
    LaunchedEffect(isPlaying, exoPlayer) {
        try {
            exoPlayer?.playWhenReady = isPlaying
        } catch (e: Exception) {
            // Ignore player state errors
        }
    }
    
    // Update playback speed when it changes
    LaunchedEffect(playbackSpeed, exoPlayer) {
        try {
            exoPlayer?.setPlaybackSpeed(playbackSpeed)
        } catch (e: Exception) {
            // Ignore playback speed errors
        }
    }
    
    // Listen to player events and handle cleanup
    val playerListener = remember(exoPlayer) {
        object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                // Only update isPlaying if we're not currently seeking
                if (!isSeeking) {
                    isPlaying = playing
                }
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                try {
                    if (playbackState == Player.STATE_READY) {
                        duration = exoPlayer?.duration ?: 0L
                    }
                } catch (e: Exception) {
                    // Ignore duration errors
                }
            }
            
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                try {
                    if (!isSeeking) {
                        currentPosition = exoPlayer?.currentPosition ?: 0L
                    }
                } catch (e: Exception) {
                    // Ignore position errors
                }
            }
            
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                playerError = error.message
            }
        }
    }
    
    LaunchedEffect(exoPlayer) {
        if (exoPlayer == null) return@LaunchedEffect
        
        exoPlayer.addListener(playerListener)
    }
    
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer?.removeListener(playerListener)
        }
    }
    
    // Periodically update the current position for real-time progress tracking
    LaunchedEffect(exoPlayer, isPlaying) {
        if (exoPlayer == null) return@LaunchedEffect
        
        try {
            while (true) {
                delay(100) // Update every 100ms for smooth progress tracking
                if (isPlaying && !isSeeking) {
                    try {
                        exoPlayer?.let { player ->
                            if (player.isPlaying) {
                                currentPosition = player.currentPosition
                                duration = player.duration
                            }
                        }
                    } catch (e: Exception) {
                        // Ignore position/duration errors
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore coroutine errors
        }
    }
    
    // Hide controls after a delay
    LaunchedEffect(showControls, isPlaying) {
        try {
            if (showControls && isPlaying) {
                delay(3000) // Hide controls after 3 seconds
                showControls = false
            }
        } catch (e: Exception) {
            // Ignore delay errors
        }
    }
    
    // Hide forward/rewind indicators after a delay
    LaunchedEffect(showForwardIndicator) {
        if (showForwardIndicator) {
            delay(500) // Hide after 500ms
            showForwardIndicator = false
        }
    }
    
    LaunchedEffect(showRewindIndicator) {
        if (showRewindIndicator) {
            delay(500) // Hide after 500ms
            showRewindIndicator = false
        }
    }
    
    // Clean up player
    DisposableEffect(exoPlayer) {
        onDispose {
            try {
                exoPlayer?.release()
            } catch (e: Exception) {
                // Ignore release errors
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { offset -> 
                        // Calculate if the tap is on the left or right side
                        val screenWidth = size.width
                        val tapX = offset.x
                        
                        // Store the playing state before seeking
                        wasPlayingBeforeSeek = isPlaying
                        isSeeking = true
                        
                        if (tapX < screenWidth / 2) {
                            // Left side - rewind specified seconds
                            try {
                                exoPlayer?.let { player ->
                                    val seekTimeMs = videoPlayerSettings.seekTimeSeconds * 1000L
                                    val newPosition = (player.currentPosition - seekTimeMs).coerceAtLeast(0L)
                                    player.seekTo(newPosition)
                                    currentPosition = newPosition
                                    showRewindIndicator = true
                                    // Keep the player playing during seeking if it was playing before
                                    if (wasPlayingBeforeSeek) {
                                        player.playWhenReady = true
                                    }
                                }
                            } catch (e: Exception) {
                                // Ignore seek errors
                            }
                        } else {
                            // Right side - forward specified seconds
                            try {
                                exoPlayer?.let { player ->
                                    val seekTimeMs = videoPlayerSettings.seekTimeSeconds * 1000L
                                    val newPosition = (player.currentPosition + seekTimeMs).coerceAtMost(player.duration)
                                    player.seekTo(newPosition)
                                    currentPosition = newPosition
                                    showForwardIndicator = true
                                    // Keep the player playing during seeking if it was playing before
                                    if (wasPlayingBeforeSeek) {
                                        player.playWhenReady = true
                                    }
                                }
                            } catch (e: Exception) {
                                // Ignore seek errors
                            }
                        }
                        
                        // Reset seeking state after a short delay using a coroutine scope
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(500) // Reset after 500ms
                            isSeeking = false
                            // Restore the playing state after seeking is finished
                            try {
                                exoPlayer?.playWhenReady = wasPlayingBeforeSeek
                                // Update isPlaying state to match the player's actual state
                                isPlaying = wasPlayingBeforeSeek
                            } catch (e: Exception) {
                                // Ignore errors
                            }
                        }
                    },
                    onTap = {
                        showControls = !showControls
                        // Reset the auto-hide timer when controls are shown
                        if (showControls && isPlaying) {
                            // The LaunchedEffect above will handle the auto-hide
                        }
                    }
                )
            }
            // Add swipe gesture detection for brightness and volume control
            .pointerInput(Unit) {
                var initialX = 0f
                var initialY = 0f
                var isTracking = false
                var trackingSide: String? = null // "left" for brightness, "right" for volume
                var initialBrightness = 0f
                var initialVolume = 0f
                
                detectDragGestures(
                    onDragStart = { offset ->
                        initialX = offset.x
                        initialY = offset.y
                        isTracking = true
                        
                        // Determine which side of the screen the gesture started on
                        val screenWidth = size.width
                        if (initialX < screenWidth * 0.5f) {
                            trackingSide = "left" // Left side for brightness
                        } else {
                            trackingSide = "right" // Right side for volume
                        }
                        
                        // Get initial brightness and volume levels
                        when (trackingSide) {
                            "left" -> {
                                val window = (context as Activity).window
                                val layoutParams = window.attributes
                                initialBrightness = layoutParams.screenBrightness
                                if (initialBrightness < 0) {
                                    // If brightness is set to system default, get the current system brightness
                                    try {
                                        val brightnessMode = Settings.System.getInt(
                                            context.contentResolver,
                                            Settings.System.SCREEN_BRIGHTNESS_MODE
                                        )
                                        if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                                            // For automatic brightness, we'll use a default value
                                            initialBrightness = 0.5f // Default to 50%
                                        } else {
                                            val systemBrightness = Settings.System.getInt(
                                                context.contentResolver,
                                                Settings.System.SCREEN_BRIGHTNESS
                                            )
                                            initialBrightness = systemBrightness / 255f
                                        }
                                    } catch (e: Exception) {
                                        initialBrightness = 0.5f // Default to 50%
                                    }
                                }
                                brightnessLevel = initialBrightness
                                showBrightnessIndicator = true
                            }
                            "right" -> {
                                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                                initialVolume = currentVolume.toFloat()
                                volumeLevel = initialVolume / maxVolume.toFloat()
                                showVolumeIndicator = true
                            }
                        }
                    },
                    onDragEnd = {
                        isTracking = false
                        trackingSide = null
                        
                        // Hide indicators after a delay
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1000)
                            showBrightnessIndicator = false
                            showVolumeIndicator = false
                        }
                    },
                    onDragCancel = {
                        isTracking = false
                        trackingSide = null
                        showBrightnessIndicator = false
                        showVolumeIndicator = false
                    },
                    onDrag = { change, dragAmount ->
                        if (!isTracking) return@detectDragGestures
                        
                        val dragY = dragAmount.y
                        
                        // Only process vertical drag gestures
                        if (abs(dragY) > abs(change.position.x - initialX) * 0.5f) {
                            when (trackingSide) {
                                "left" -> {
                                    // Adjust brightness based on vertical drag (up = increase, down = decrease)
                                    val delta = -dragY * 0.01f // Invert Y axis (up is negative)
                                    brightnessLevel = (initialBrightness + delta).coerceIn(0f, 1f)
                                    
                                    try {
                                        // Apply brightness change to the current window
                                        val window = (context as Activity).window
                                        val layoutParams = window.attributes
                                        layoutParams.screenBrightness = brightnessLevel
                                        window.attributes = layoutParams
                                        
                                        // Also try to change system brightness if we have permission
                                        if (Settings.System.canWrite(context)) {
                                            Settings.System.putInt(
                                                context.contentResolver,
                                                Settings.System.SCREEN_BRIGHTNESS,
                                                (brightnessLevel * 255).toInt()
                                            )
                                        }
                                        
                                        // Update UI indicator
                                        showBrightnessIndicator = true
                                    } catch (e: Exception) {
                                        // Ignore permission or other errors
                                    }
                                }
                                "right" -> {
                                    // Adjust volume based on vertical drag (up = increase, down = decrease)
                                    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                                    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                    val delta = -dragY * 0.01f * maxVolume // Invert Y axis (up is negative)
                                    val newVolume = (initialVolume + delta).coerceIn(0f, maxVolume.toFloat())
                                    
                                    try {
                                        // Apply volume change
                                        audioManager.setStreamVolume(
                                            AudioManager.STREAM_MUSIC,
                                            newVolume.toInt(),
                                            0 // No flags
                                        )
                                        
                                        // Update volume level for UI indicator
                                        volumeLevel = newVolume / maxVolume.toFloat()
                                        
                                        // Update UI indicator
                                        showVolumeIndicator = true
                                    } catch (e: Exception) {
                                        // Ignore permission or other errors
                                    }
                                }
                            }
                        }
                    }
                )
            }
    ) {
        // Show error message if player failed to initialize
        if (playerError != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = playerError ?: "Unknown error occurred",
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            }
            return@Box
        }
        
        // Check if player is initialized
        if (exoPlayer == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Initializing player...",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
            return@Box
        }
        
        // Video player
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // We're using our own controls
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    // Make the player view fill the entire screen
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    
                    // Apply subtitle settings to the player view
                    // Set subtitle styling with custom font
                    setSubtitleTextSize(subtitleSettings.textSize)
                    setSubtitleColors(subtitleSettings, customTypeface)
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { playerView ->
                // Update the player view when subtitle settings change
                // Update subtitle styling when settings change
                playerView.setSubtitleTextSize(subtitleSettings.textSize)
                playerView.setSubtitleColors(subtitleSettings, customTypeface)
            }
        )
        
        // Rewind indicator
        if (showRewindIndicator) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "Rewind ${videoPlayerSettings.seekTimeSeconds} seconds",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "${videoPlayerSettings.seekTimeSeconds}s",
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        // Forward indicator
        if (showForwardIndicator) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward,
                        contentDescription = "Forward ${videoPlayerSettings.seekTimeSeconds} seconds",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "${videoPlayerSettings.seekTimeSeconds}s",
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        // Brightness indicator
        if (showBrightnessIndicator) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BrightnessMedium,
                        contentDescription = "Brightness",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "${(brightnessLevel * 100).toInt()}%",
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        // Volume indicator
        if (showVolumeIndicator) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "${(volumeLevel * 100).toInt()}%",
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        // Custom controls overlay
        if (showControls) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                // Top bar with back button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
                
                // Middle play/pause button
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                // Bottom controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(16.dp)
                ) {
                    // Progress slider
                    Slider(
                        value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                        onValueChange = { progress ->
                            // Store the playing state before seeking
                            if (!isSeeking) {
                                wasPlayingBeforeSeek = isPlaying
                            }
                            isSeeking = true
                            val newPosition = (progress * duration).toLong()
                            try {
                                exoPlayer?.seekTo(newPosition)
                                currentPosition = newPosition
                                // Keep the player playing during seeking if it was playing before
                                if (wasPlayingBeforeSeek) {
                                    exoPlayer?.playWhenReady = true
                                }
                            } catch (e: Exception) {
                                // Ignore seek errors
                            }
                        },
                        onValueChangeFinished = {
                            isSeeking = false
                            // Restore the playing state after seeking is finished
                            try {
                                exoPlayer?.playWhenReady = wasPlayingBeforeSeek
                                // Update isPlaying state to match the player's actual state
                                isPlaying = wasPlayingBeforeSeek
                            } catch (e: Exception) {
                                // Ignore errors
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Time and controls row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(currentPosition),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontManager.loadFontFamily(context, fontSettings.fontType)
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Video speed controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { showSpeedDropdown = true }
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = "Playback speed",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    
                                    Text(
                                        text = String.format("%.2fx", playbackSpeed),
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                        fontFamily = FontManager.loadFontFamily(context, fontSettings.fontType)
                                    )
                                }
                                
                                DropdownMenu(
                                    expanded = showSpeedDropdown,
                                    onDismissRequest = { showSpeedDropdown = false },
                                    modifier = Modifier.background(Color.Black)
                                ) {
                                    speedOptions.forEach { speed ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = String.format("%.2fx", speed),
                                                    color = if (speed == playbackSpeed) MaterialTheme.colorScheme.primary else Color.White,
                                                    fontFamily = FontManager.loadFontFamily(context, fontSettings.fontType)
                                                )
                                            },
                                            onClick = {
                                                playbackSpeed = speed
                                                showSpeedDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            // Normal speed button
                            Text(
                                text = "Normal",
                                color = if (playbackSpeed == 1.0f) MaterialTheme.colorScheme.primary else Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (playbackSpeed == 1.0f) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .clickable { playbackSpeed = 1.0f }
                                    .padding(4.dp),
                                fontFamily = FontManager.loadFontFamily(context, fontSettings.fontType)
                            )
                        }
                        
                        Text(
                            text = formatTime(duration),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontManager.loadFontFamily(context, fontSettings.fontType)
                        )
                    }
                }
            }
        }
    }
}

fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, remainingMinutes, remainingSeconds)
    } else {
        String.format("%02d:%02d", remainingMinutes, remainingSeconds)
    }
}