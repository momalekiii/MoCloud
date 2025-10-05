# CCloud - Movie & TV Series Streaming App

<p align="center">
  <img src="app/src/main/res/drawable/splash_logo.png" alt="CCloud Logo" width="150"/>
</p>

CCloud is a modern Android streaming application built with Jetpack Compose and Kotlin. The app provides a sleek user interface for browsing movies and TV series, playing videos, and customizing the viewing experience. With a focus on user experience and performance, CCloud delivers high-quality streaming content in an intuitive and visually appealing interface.

## 🚀 Key Features

### 🎬 Content Discovery
- **Browse Movies & Series**: Attractive grid layout for discovering content
- **Powerful Search**: Search across both movies and TV series with real-time results
- **Detailed Information**: Comprehensive details for each movie and series including ratings, genres, and descriptions

### ▶️ Media Playback
- **Custom Video Player**: Built with ExoPlayer for smooth video playback
- **Multiple Quality Options**: Choose from various quality options for playback and download
- **Download Options**: Multiple download methods including browser, ADM, and VLC
- **Fullscreen Experience**: Optimized landscape mode for immersive viewing

### 🎨 Personalization
- **Theme Customization**: Light, dark, and system themes with color options
- **Subtitle Settings**: Customize subtitle appearance (background, text color, border, size)
- **Responsive UI**: Material Design 3 interface that adapts to all screen sizes

### 📱 User Experience
- **Intuitive Navigation**: Bottom navigation for easy access to all sections
- **Error Handling**: Retry mechanisms for failed requests
- **Loading States**: Shimmer loading animations for smooth user experience
- **Season & Episode Management**: Easy navigation through TV series seasons and episodes

## 🛠️ Tech Stack

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern toolkit for building native UI
- **ExoPlayer** - Media playback library
- **Kotlin Serialization** - JSON serialization
- **Coil** - Image loading library
- **Material Design 3** - UI components and design system
- **Navigation Component** - For seamless screen transitions
- **ViewModel & LiveData** - For state management
- **Coroutines** - For asynchronous operations
- **OkHttp** - For network requests

## 🏗️ Architecture

The app follows a clean architecture pattern with the following components:

- **UI Layer** - Jetpack Compose screens and components
- **Data Layer** - Models and repositories for data management
- **Domain Layer** - Business logic and use cases
- **Utils** - Helper classes and extensions
- **Navigation** - Single-activity architecture with Compose Navigation
- **State Management** - ViewModel for UI state handling

## Getting Started

### Prerequisites

- Android Studio Ladybug or later
- Android SDK API 36 (Android 16)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/code3-dev/CCloud.git
   ```

2. Open the project in Android Studio

3. Build and run the project

### Building

To build the debug APK:
```bash
./gradlew assembleDebug
```

To build the release APK:
```bash
./gradlew assembleRelease
```

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/pira/ccloud/
│   │   │   ├── data/           # Data models and repositories
│   │   │   ├── navigation/     # Navigation graph
│   │   │   ├── screens/        # Compose screens
│   │   │   ├── ui/             # Theme and UI components
│   │   │   ├── utils/          # Utility classes
│   │   │   ├── MainActivity.kt
│   │   │   └── VideoPlayerActivity.kt
│   │   └── res/                # Resources
│   └── test/                   # Unit tests
├── build.gradle.kts            # App build configuration
└── ...
```

## Key Components

### Video Player

The app features a custom video player built with ExoPlayer that supports:

- Play/Pause controls
- Progress seeking
- Subtitle customization
- Fullscreen landscape mode

### Settings

Users can customize their experience through the settings screen:

- Theme mode (Light/Dark/System)
- Primary and secondary color selection
- Subtitle settings (colors and text size)

## 📱 App Screens

### 🎬 Movies Screen
- Browse latest movies in an attractive grid layout
- Pull-to-refresh functionality
- Infinite scrolling for loading more content
- Error handling with retry option
- Shimmer loading animations

### 📺 Series Screen
- Browse TV series with comprehensive information
- Season and episode organization
- Pull-to-refresh and infinite scrolling
- Error handling with retry option

### 🔍 Search Screen
- Real-time search across movies and series
- Instant results as you type
- Error handling with retry button
- Clear search functionality

### 📄 Single Movie Screen
- Detailed movie information
- Multiple quality options for playback
- Download options (Browser, ADM, VLC)
- Custom video player integration

### 📺 Single Series Screen
- Comprehensive series details
- Season navigation and episode listing
- Multiple quality options per episode
- Download options for each episode
- Error handling with retry functionality

### ⚙️ Settings Screen
- Theme customization (Light/Dark/System)
- Primary and secondary color selection
- Subtitle settings (colors and text size)
- Reset to default options

### 🏠 Bottom Navigation
The app uses a bottom navigation bar to switch between:
- Movies screen
- Series screen
- Search screen
- Settings screen

## 🤝 Contributing

We welcome contributions to CCloud! Here's how you can help:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a pull request

Please ensure your code follows the existing style and includes appropriate tests.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [ExoPlayer](https://exoplayer.dev/) for media playback
- [Coil](https://coil-kt.github.io/coil/) for image loading
- [Material Design](https://m3.material.io/) for UI components

## 📞 Contact

For support or inquiries, please open an issue on GitHub.