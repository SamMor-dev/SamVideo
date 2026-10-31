package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Copyright (c) 2026, Sam Moreno.
 * All rights reserved.
 * Licensed under the BSD 2-Clause License.
 */

sealed class GestureFeedback {
    data class Volume(val percent: Int) : GestureFeedback()
    data class Brightness(val percent: Int) : GestureFeedback()
    data class Seek(val deltaSeconds: Int, val isForward: Boolean) : GestureFeedback()
}

@Composable
fun GestureIndicatorOverlay(
    feedback: GestureFeedback?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = feedback != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        if (feedback == null) return@AnimatedVisibility

        val soberAccent = MaterialTheme.colorScheme.primary

        Box(
            modifier = Modifier
                .background(Color(0xE61E2430), shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (feedback) {
                is GestureFeedback.Volume -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.VolumeUp,
                            contentDescription = "Volume",
                            tint = soberAccent,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Volume: ${feedback.percent}%",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { feedback.percent / 100f },
                            modifier = Modifier
                                .height(4.dp)
                                .size(width = 100.dp, height = 4.dp),
                            color = soberAccent,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
                is GestureFeedback.Brightness -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.BrightnessMedium,
                            contentDescription = "Brilho",
                            tint = soberAccent,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Brilho: ${feedback.percent}%",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { feedback.percent / 100f },
                            modifier = Modifier
                                .height(4.dp)
                                .size(width = 100.dp, height = 4.dp),
                            color = soberAccent,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
                is GestureFeedback.Seek -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (feedback.isForward) Icons.Rounded.FastForward else Icons.Rounded.FastRewind,
                            contentDescription = if (feedback.isForward) "Avançar" else "Retroceder",
                            tint = soberAccent,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${if (feedback.isForward) "+" else "-"}${feedback.deltaSeconds}s",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
