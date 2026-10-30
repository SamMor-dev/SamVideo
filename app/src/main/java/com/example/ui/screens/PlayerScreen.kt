package com.example.ui.screens

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Rational
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ui.components.PlayerControlsOverlay
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryCyan
import com.example.viewmodel.PlayerViewModel

/*
 * Copyright (c) 2026, Sam Moreno.
 * All rights reserved.
 * Licensed under the BSD 2-Clause License.
 */

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uiState by viewModel.uiState.collectAsState()

    // Trata o botão Voltar do sistema Android
    BackHandler {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        onNavigateBack()
    }

    // Modo imersivo: esconde barras de navegação e status quando os controles estão ocultos
    LaunchedEffect(uiState.isControlsVisible) {
        activity?.window?.let { window ->
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (uiState.isControlsVisible) {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            } else {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Ao sair do player, restaura as barras do sistema e orientação
    DisposableEffect(Unit) {
        onDispose {
            activity?.window?.let { window ->
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("player_screen_root")
    ) {
        // Player de vídeo com SurfaceView acelerado por hardware
        VideoPlayerView(
            viewModel = viewModel,
            uiState = uiState
        )

        // Camada de controles do player
        PlayerControlsOverlay(
            uiState = uiState,
            onBackClick = {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                onNavigateBack()
            },
            onPlayPauseClick = { viewModel.togglePlayPause() },
            onRewindClick = { viewModel.seekBackward(10000L) },
            onForwardClick = { viewModel.seekForward(10000L) },
            onSeekTo = { pos -> viewModel.seekToPosition(pos) },
            onAspectRatioClick = { viewModel.cycleAspectRatio() },
            onRotateClick = {
                activity?.let { act ->
                    val currentOrientation = act.requestedOrientation
                    if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        viewModel.togglePortraitLock()
                    } else {
                        act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        viewModel.togglePortraitLock()
                    }
                }
            },
            onPipClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val aspectRatio = if (uiState.videoWidth > 0 && uiState.videoHeight > 0) {
                        val width = uiState.videoWidth.coerceIn(1, 10000)
                        val height = uiState.videoHeight.coerceIn(1, 10000)
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
                    activity?.enterPictureInPictureMode(params)
                }
            },
            onSpeedSelected = { speed -> viewModel.setPlaybackSpeed(speed) }
        )

        // Diálogo de erro amigável se o arquivo não puder ser lido
        if (uiState.errorMessage != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                shape = RoundedCornerShape(16.dp),
                color = BackgroundDark.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Aviso de Reprodução",
                        color = ErrorRed,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onNavigateBack
                        ) {
                            Text("Voltar", color = Color.White)
                        }
                        Button(
                            onClick = { viewModel.retry() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                        ) {
                            Text("Tentar Novamente", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
