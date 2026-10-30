package com.example.model

import android.net.Uri
import java.util.Locale
import java.util.concurrent.TimeUnit

/*
 * Copyright (c) 2026, Sam Moreno.
 * All rights reserved.
 * Licensed under the BSD 2-Clause License.
 */

/**
 * Representa um vídeo disponível para reprodução no SamVideo.
 *
 * @param id Identificador único no MediaStore ou gerado
 * @param uri URI seguro para reprodução com ExoPlayer
 * @param title Nome amigável do arquivo de vídeo
 * @param durationMs Duração em milissegundos
 * @param sizeBytes Tamanho em bytes
 * @param dateModified Data de modificação em epoch segundos
 * @param width Largura em pixels (se disponível)
 * @param height Altura em pixels (se disponível)
 * @param isSample Indica se é um vídeo demonstrativo pré-carregado
 */
data class VideoItem(
    val id: Long,
    val uri: Uri,
    val title: String,
    val durationMs: Long = 0L,
    val sizeBytes: Long = 0L,
    val dateModified: Long = 0L,
    val width: Int = 0,
    val height: Int = 0,
    val isSample: Boolean = false
) {
    /**
     * Retorna a duração formatada em mm:ss ou hh:mm:ss
     */
    val formattedDuration: String
        get() {
            if (durationMs <= 0L) return "00:00"
            val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
            return if (hours > 0) {
                String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(Locale.US, "%02d:%02d", minutes, seconds)
            }
        }

    /**
     * Retorna o tamanho do arquivo em formato legível (MB, GB)
     */
    val formattedSize: String
        get() {
            if (sizeBytes <= 0L) return ""
            val kb = sizeBytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> String.format(Locale.US, "%.1f GB", gb)
                mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
                else -> String.format(Locale.US, "%.0f KB", kb)
            }
        }

    /**
     * Retorna a resolução do vídeo (ex: 1080p, 4K, 720p)
     */
    val resolutionLabel: String
        get() {
            if (width <= 0 || height <= 0) return ""
            val minDim = minOf(width, height)
            return when {
                minDim >= 2160 -> "4K"
                minDim >= 1440 -> "2K"
                minDim >= 1080 -> "1080p"
                minDim >= 720 -> "720p"
                minDim >= 480 -> "480p"
                else -> "${width}x${height}"
            }
        }
}
