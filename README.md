# SamVideo 🎬⚡

[![License](https://img.shields.io/badge/License-BSD_2--Clause-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84.svg?logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple.svg?logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Media3](https://img.shields.io/badge/Engine-AndroidX%20Media3%20ExoPlayer-brightgreen.svg)](https://developer.android.com/media/media3)

**SamVideo** is a lightweight, modular, high-performance Android video player built with pure **Kotlin**, **Jetpack Compose (Material Design 3)**, and powered by **AndroidX Media3 (ExoPlayer)**. 

Designed with an executive, high-contrast UI and zero-copy hardware acceleration, SamVideo delivers instant startup, minimal battery usage, and fluid playback for local device videos and SAF file picker integrations.

> **Author:** Sam Moreno  
> **License:** BSD 2-Clause License

---

## 👩‍💻 Welcome, Developers! (Quick Start Guide)

New to the codebase? Here is everything you need to get up and running in minutes.

### Prerequisites
* **Android Studio:** Ladybug / Koala or newer.
* **JDK:** Java 17 or higher.
* **Android SDK:** Compile SDK 36, Min SDK 24 (Android 7.0+).

### 1. Clone & Open
```bash
git clone https://github.com/SamMor-dev/SamVideo.git
```
1. Open **Android Studio**.
2. Select **File > Open** and choose the `SamVideo` folder.
3. Allow Gradle to sync automatically.

### 2. Run the App
1. Connect an Android device with **USB Debugging** enabled or start an Android Emulator (API 24+).
2. Click the green **Run (`▶`)** button in Android Studio (or press `Shift + F10`).

### 3. Run Unit Tests
To verify all unit tests and codebase integrity:
```bash
./gradlew testDebugUnitTest
```

---

## 🚀 Technical Highlights & Architecture

1. **Pure Hardware Acceleration (Zero-Copy):**
   - Configured with `DefaultRenderersFactory` disabling slow software fallback renderers (`EXTENSION_RENDERER_MODE_OFF`).
   - Direct GPU/VPU hardware decoding via `MediaCodec` and `SurfaceView` overlay for maximum framerate and minimal battery drain.

2. **Modern MVVM & Reactive UI:**
   - Built entirely with **Jetpack Compose** and **Kotlin Coroutines / StateFlow**.
   - Decoupled `PlayerViewModel` managing player lifecycle, playback state, seeking, speed control, and theme modes.

3. **Feature Set:**
   - **Local MediaStore & SAF Integration:** Automatically indexes local device videos and supports opening any local file (`MP4`, `MKV`, `WebM`, `3GP`, `TS`) via Android Storage Access Framework.
   - **Immersive HUD & Gestures:**
     - Double-tap left/right: Seek backward/forward 10s.
     - Vertical drag (left): Adjust screen brightness.
     - Vertical drag (right): Adjust media volume.
   - **Aspect Ratio Modes:** Fit, Zoom, and Fill.
   - **Picture-in-Picture (PiP):** Native floating window support.
   - **Theme Selector:** Dark (AMOLED black), Light, and Slate Blue palettes.

---

## 📂 Codebase Directory Structure

```
app/src/main/java/com/example/
├── MainActivity.kt                # App entry point, reactive theme, PiP & intent handling
├── data/
│   └── VideoRepository.kt        # MediaStore local video queries & SAF URI resolver
├── model/
│   └── VideoItem.kt              # Video data model (duration, size, resolution calculations)
├── player/
│   ├── AspectRatioMode.kt        # Aspect ratio display modes (FIT, ZOOM, FILL)
│   └── PlaybackState.kt          # Reactive player UI state container
├── ui/
│   ├── components/
│   │   ├── AboutDialog.kt             # Author & BSD 2-Clause license modal dialog
│   │   ├── GestureIndicatorOverlay.kt  # Visual feedback for volume, brightness, and seek gestures
│   │   ├── PlayerControlsOverlay.kt    # Fullscreen HUD (Seekbar, aspect ratio, PiP, speed)
│   │   ├── ThemeSelectionDialog.kt     # Theme picker (Dark, Light, Slate Blue)
│   │   └── VideoPlayerView.kt          # Hardware-accelerated SurfaceView wrapper
│   ├── screens/
│   │   ├── PlayerScreen.kt             # Immersive playback screen with BackHandler
│   │   └── VideoListScreen.kt          # Clean video browser with search & file picker
│   └── theme/
│       ├── Color.kt                    # Solid color palettes (No gradients)
│       ├── Theme.kt                    # Material 3 theme configuration & insets
│       ├── ThemeMode.kt                # Theme mode enum
│       └── Type.kt                     # Material 3 typography
└── viewmodel/
    └── PlayerViewModel.kt        # Reactive player lifecycle & state management
```

---

## 📜 License

Distributed under the **BSD 2-Clause License**. See [LICENSE](LICENSE) for details.

```
Copyright (c) 2026, Sam Moreno. All rights reserved.
```
