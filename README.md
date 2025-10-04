# CCloud - Movie Streaming App

CCloud is a modern Android movie streaming application built with Jetpack Compose and Kotlin. The app provides a sleek user interface for browsing movies, playing videos, and customizing the viewing experience.

## Features

- Browse movies with an attractive grid layout
- View detailed information for each movie
- Stream videos with a custom video player
- Customize subtitle settings (background color, text color, border color, text size)
- Theme customization (light/dark mode with color options)
- Responsive UI with Material Design 3

## Tech Stack

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern toolkit for building native UI
- **ExoPlayer** - Media playback library
- **Kotlin Serialization** - JSON serialization
- **Coil** - Image loading library
- **Material Design 3** - UI components and design system

## Architecture

The app follows a clean architecture pattern with the following components:

- **UI Layer** - Compose screens and components
- **Data Layer** - Models and repositories
- **Domain Layer** - Business logic
- **Utils** - Helper classes and extensions

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

### Navigation

The app uses a bottom navigation bar to switch between:

- Movies screen
- Search screen
- Settings screen
- ...

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [ExoPlayer](https://exoplayer.dev/) for media playback
- [Coil](https://coil-kt.github.io/coil/) for image loading
- [Material Design](https://m3.material.io/) for UI components