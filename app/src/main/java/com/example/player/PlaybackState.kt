package com.example.player

import androidx.media3.common.Player

/*
 * Copyright (c) 2026, Sam Moreno.
 * All rights reserved.
 * Licensed under the BSD 2-Clause License.
 */

/**
 * Estado reativo do reprodutor de vídeo no SamVideo.
 */
data class PlaybackUiState(
    val isPlaying: Boolean = false,
    val playbackState: Int = Player.STATE_IDLE,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val videoTitle: String = "",
    val aspectRatioMode: AspectRatioMode = AspectRatioMode.FIT,
    val isControlsVisible: Boolean = true,
    val errorMessage: String? = null,
    val videoWidth: Int = 0,
    val videoHeight: Int = 0,
    val isPortraitLocked: Boolean = false
) {
    val isBuffering: Boolean
        get() = playbackState == Player.STATE_BUFFERING

    val isEnded: Boolean
        get() = playbackState == Player.STATE_ENDED

    val isReady: Boolean
        get() = playbackState == Player.STATE_READY

    /**
     * Progresso atual normalizado de 0f a 1f para a Seekbar
     */
    val progress: Float
        get() = if (durationMs > 0) {
            (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f

    /**
     * Progresso do buffer normalizado de 0f a 1f
     */
    val bufferProgress: Float
        get() = if (durationMs > 0) {
            (bufferedPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f
}
