package com.example

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import com.example.model.VideoItem
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.VideoListScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.PlayerViewModel

/*
 * Copyright (c) 2026, Sam Moreno.
 * All rights reserved.
 * Licensed under the BSD 2-Clause License.
 */

enum class CurrentScreen {
    VIDEO_LIST,
    PLAYER
}

class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()
    private var currentScreen by mutableStateOf(CurrentScreen.VIDEO_LIST)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Trata intents externos para abrir vídeos de gerenciadores de arquivos
        handleIntent(intent)

        setContent {
            val currentTheme by playerViewModel.themeMode.collectAsState()
            MyApplicationTheme(themeMode = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            CurrentScreen.VIDEO_LIST -> {
                                VideoListScreen(
                                    viewModel = playerViewModel,
                                    onVideoSelected = {
                                        currentScreen = CurrentScreen.PLAYER
                                    }
                                )
                            }
                            CurrentScreen.PLAYER -> {
                                PlayerScreen(
                                    viewModel = playerViewModel,
                                    onNavigateBack = {
                                        playerViewModel.player.pause()
                                        currentScreen = CurrentScreen.VIDEO_LIST
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.let { uri ->
                playerViewModel.openVideoUri(uri)
                currentScreen = CurrentScreen.PLAYER
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // Entra automaticamente em PiP se um vídeo estiver em reprodução
        if (currentScreen == CurrentScreen.PLAYER &&
            playerViewModel.player.isPlaying &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        ) {
            val state = playerViewModel.uiState.value
            val aspectRatio = if (state.videoWidth > 0 && state.videoHeight > 0) {
                val width = state.videoWidth.coerceIn(1, 10000)
                val height = state.videoHeight.coerceIn(1, 10000)
                val ratio = width.toFloat() / height.toFloat()
                when {
                    ratio > 2.38f -> Rational(238, 100)
                    ratio < 0.42f -> Rational(42, 100)
                    else -> Rational(width, height)
                }
            } else {
                Rational(16, 9)
            }

            val params = PictureInPictureParams.Builder()
                .setAspectRatio(aspectRatio)
                .build()
            enterPictureInPictureMode(params)
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            // No modo PiP, oculta controles para exibição limpa
            playerViewModel.player.play()
        }
    }

    override fun onStop() {
        super.onStop()
        // Pausa reprodução se não estiver em PiP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !isInPictureInPictureMode) {
            playerViewModel.player.pause()
        }
    }
}
