package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.example.player.PlaybackUiState
import com.example.viewmodel.PlayerViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/*
 * Copyright (c) 2026, Sam Moreno.
 * All rights reserved.
 * Licensed under the BSD 2-Clause License.
 */

/**
 * Componente que encapsula o PlayerView da AndroidX Media3 com SurfaceView
 * acelerado diretamente por hardware e suporte a gestos de toque modernos.
 */
@androidx.media3.common.util.UnstableApi
@Composable
fun VideoPlayerView(
    viewModel: PlayerViewModel,
    uiState: PlaybackUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val scope = rememberCoroutineScope()

    var gestureFeedback by remember { mutableStateOf<GestureFeedback?>(null) }
    var feedbackHideJob by remember { mutableStateOf<Job?>(null) }

    fun showFeedback(feedback: GestureFeedback) {
        gestureFeedback = feedback
        feedbackHideJob?.cancel()
        feedbackHideJob = scope.launch {
            delay(1200L)
            gestureFeedback = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            feedbackHideJob?.cancel()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // PlayerView nativo do Media3 (SurfaceView acelerado por hardware)
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    useController = false
                    player = viewModel.player
                    resizeMode = uiState.aspectRatioMode.resizeMode
                    keepScreenOn = true
                }
            },
            update = { playerView ->
                playerView.player = viewModel.player
                playerView.resizeMode = uiState.aspectRatioMode.resizeMode
                playerView.keepScreenOn = uiState.isPlaying
            },
            modifier = Modifier.fillMaxSize()
        )

        // Camada transparente de captura de gestos
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            viewModel.toggleControlsVisibility()
                        },
                        onDoubleTap = { offset ->
                            val screenWidth = size.width
                            when {
                                offset.x < screenWidth * 0.35f -> {
                                    // Toque duplo à esquerda -> retrocede 10s
                                    viewModel.seekBackward(10000L)
                                    showFeedback(GestureFeedback.Seek(10, isForward = false))
                                }
                                offset.x > screenWidth * 0.65f -> {
                                    // Toque duplo à direita -> avança 10s
                                    viewModel.seekForward(10000L)
                                    showFeedback(GestureFeedback.Seek(10, isForward = true))
                                }
                                else -> {
                                    // Toque duplo no centro -> Play / Pause
                                    viewModel.togglePlayPause()
                                }
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            // Encerrou arraste vertical
                        },
                        onVerticalDrag = { change, dragAmount ->
                            val screenWidth = size.width
                            val xPos = change.position.x
                            val delta = -dragAmount / 400f

                            if (xPos < screenWidth / 2f) {
                                // Lado esquerdo: Controle de Brilho da Tela
                                activity?.let { act ->
                                    val window = act.window
                                    val currentBrightness = window.attributes.screenBrightness
                                    val safeBrightness = if (currentBrightness < 0f) 0.5f else currentBrightness
                                    val newBrightness = (safeBrightness + delta).coerceIn(0.05f, 1f)
                                    val lp = window.attributes
                                    lp.screenBrightness = newBrightness
                                    window.attributes = lp
                                    showFeedback(GestureFeedback.Brightness((newBrightness * 100).roundToInt()))
                                }
                            } else {
                                // Lado direito: Controle de Volume de Mídia
                                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                                val volumeChange = (-dragAmount / 50f).roundToInt()
                                if (volumeChange != 0) {
                                    val newVolume = (currentVolume + volumeChange).coerceIn(0, maxVolume)
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
                                    val percent = (newVolume.toFloat() / maxVolume.toFloat() * 100).roundToInt()
                                    showFeedback(GestureFeedback.Volume(percent))
                                }
                            }
                        }
                    )
                }
        )

        // Overlay central para feedback de Volume/Brilho/Seek
        GestureIndicatorOverlay(
            feedback = gestureFeedback,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
