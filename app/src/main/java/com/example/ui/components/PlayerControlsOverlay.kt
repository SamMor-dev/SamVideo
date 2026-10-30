package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PictureInPicture
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.player.PlaybackUiState
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.PrimaryCyan
import java.util.Locale
import java.util.concurrent.TimeUnit

/*
 * Copyright (c) 2026, Sam Moreno.
 * All rights reserved.
 * Licensed under the BSD 2-Clause License.
 */

@Composable
fun PlayerControlsOverlay(
    uiState: PlaybackUiState,
    onBackClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onRewindClick: () -> Unit,
    onForwardClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onAspectRatioClick: () -> Unit,
    onRotateClick: () -> Unit,
    onPipClick: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSeeking by remember { mutableStateOf(false) }
    var seekPositionMs by remember { mutableFloatStateOf(0f) }
    var showSpeedMenu by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = uiState.isControlsVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.85f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.88f)
                        )
                    )
                )
        ) {
            // === BARRA SUPERIOR (Topo) ===
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("player_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = uiState.videoTitle.ifEmpty { "Reproduzindo" },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Botão de Proporção de Tela (Aspect Ratio)
                Surface(
                    onClick = onAspectRatioClick,
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .testTag("player_aspect_ratio_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AspectRatio,
                            contentDescription = "Aspect Ratio",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.aspectRatioMode.label,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Botão de Rotação / Orientação
                IconButton(
                    onClick = onRotateClick,
                    modifier = Modifier.testTag("player_rotation_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ScreenRotation,
                        contentDescription = "Girar Tela",
                        tint = if (uiState.isPortraitLocked) AccentPurple else Color.White
                    )
                }

                // Botão PiP (Picture in Picture)
                IconButton(
                    onClick = onPipClick,
                    modifier = Modifier.testTag("player_pip_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PictureInPicture,
                        contentDescription = "Picture in Picture",
                        tint = Color.White
                    )
                }
            }

            // === CONTROLES CENTRAIS (Play/Pause, Seek 10s, Buffer) ===
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Retroceder 10s
                IconButton(
                    onClick = onRewindClick,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                        .testTag("player_rewind_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Replay10,
                        contentDescription = "Retroceder 10 segundos",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(36.dp))

                // Play / Pause / Buffering / Replay
                Box(contentAlignment = Alignment.Center) {
                    if (uiState.isBuffering) {
                        CircularProgressIndicator(
                            color = PrimaryCyan,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(68.dp)
                        )
                    }

                    FilledIconButton(
                        onClick = onPlayPauseClick,
                        modifier = Modifier
                            .size(72.dp)
                            .testTag("player_play_pause_button"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = PrimaryCyan,
                            contentColor = Color.Black
                        )
                    ) {
                        val icon = when {
                            uiState.isEnded -> Icons.Rounded.Replay
                            uiState.isPlaying -> Icons.Rounded.Pause
                            else -> Icons.Rounded.PlayArrow
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = if (uiState.isPlaying) "Pausar" else "Reproduzir",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(36.dp))

                // Avançar 10s
                IconButton(
                    onClick = onForwardClick,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                        .testTag("player_forward_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Forward10,
                        contentDescription = "Avançar 10 segundos",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // === BARRA INFERIOR (Seekbar, Tempo, Velocidade) ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Seekbar com Slider deslizante
                Slider(
                    value = if (isSeeking) seekPositionMs else uiState.currentPositionMs.toFloat(),
                    onValueChange = {
                        isSeeking = true
                        seekPositionMs = it
                    },
                    onValueChangeFinished = {
                        isSeeking = false
                        onSeekTo(seekPositionMs.toLong())
                    },
                    valueRange = 0f..uiState.durationMs.coerceAtLeast(1L).toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryCyan,
                        activeTrackColor = PrimaryCyan,
                        inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("player_seekbar")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Marcador de tempo atual / total
                    val displayedPosition = if (isSeeking) seekPositionMs.toLong() else uiState.currentPositionMs
                    Text(
                        text = "${formatTime(displayedPosition)} / ${formatTime(uiState.durationMs)}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Seletor de Velocidade (0.5x .. 2.0x)
                    Box {
                        Surface(
                            onClick = { showSpeedMenu = !showSpeedMenu },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.testTag("player_speed_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Speed,
                                    contentDescription = "Velocidade",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${uiState.playbackSpeed}x",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showSpeedMenu,
                            onDismissRequest = { showSpeedMenu = false }
                        ) {
                            listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${speed}x",
                                            fontWeight = if (uiState.playbackSpeed == speed) FontWeight.Bold else FontWeight.Normal,
                                            color = if (uiState.playbackSpeed == speed) PrimaryCyan else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        onSpeedSelected(speed)
                                        showSpeedMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Converte milissegundos para formato legível mm:ss ou hh:mm:ss
 */
private fun formatTime(ms: Long): String {
    if (ms <= 0L) return "00:00"
    val hours = TimeUnit.MILLISECONDS.toHours(ms)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}
