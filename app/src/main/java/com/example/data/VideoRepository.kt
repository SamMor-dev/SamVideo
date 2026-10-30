package com.example.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * Copyright (c) 2026, Sam Moreno.
 * All rights reserved.
 * Licensed under the BSD 2-Clause License.
 */

/**
 * Repositório leve para consulta de vídeos locais via Android MediaStore
 * e fornecimento de amostras de demonstração.
 */
class VideoRepository(private val context: Context) {

    /**
     * Consulta os vídeos locais disponíveis no dispositivo.
     */
    suspend fun getLocalVideos(): List<VideoItem> = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<VideoItem>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"

        try {
            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn) ?: "Vídeo sem título"
                    val duration = cursor.getLong(durationColumn)
                    val size = cursor.getLong(sizeColumn)
                    val date = cursor.getLong(dateColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    videoList.add(
                        VideoItem(
                            id = id,
                            uri = contentUri,
                            title = name,
                            durationMs = duration,
                            sizeBytes = size,
                            dateModified = date,
                            width = width,
                            height = height,
                            isSample = false
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Em caso de falta de permissão ou erro do provider, retorna lista vazia segura
        }

        videoList
    }

    /**
     * Amostras de vídeos de teste leves para validação imediata do player
     */
    fun getSampleVideos(): List<VideoItem> {
        return listOf(
            VideoItem(
                id = -1L,
                uri = Uri.parse("https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_5MB.mp4"),
                title = "Big Buck Bunny (Amostra Full HD 1080p)",
                durationMs = 10000L,
                sizeBytes = 5238714L,
                width = 1920,
                height = 1080,
                isSample = true
            ),
            VideoItem(
                id = -2L,
                uri = Uri.parse("https://cdn.plyr.io/static/demo/View_From_A_Blue_Moon_Trailer-720p.mp4"),
                title = "View From A Blue Moon (Trailer Oficial HD)",
                durationMs = 180000L,
                sizeBytes = 72827883L,
                width = 1280,
                height = 720,
                isSample = true
            ),
            VideoItem(
                id = -3L,
                uri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-1/mp4/android-screens-10s.mp4"),
                title = "Android Screens (Demonstração ExoPlayer)",
                durationMs = 10000L,
                sizeBytes = 1854368L,
                width = 1280,
                height = 720,
                isSample = true
            ),
            VideoItem(
                id = -4L,
                uri = Uri.parse("https://test-videos.co.uk/vids/sintel/mp4/h264/720/Sintel_720_10s_1MB.mp4"),
                title = "Sintel (Amostra Open Source)",
                durationMs = 10000L,
                sizeBytes = 1047954L,
                width = 1280,
                height = 720,
                isSample = true
            )
        )
    }

    /**
     * Extrai informações de um vídeo selecionado pelo usuário via SAF / PhotoPicker
     */
    fun resolveSelectedVideo(uri: Uri): VideoItem {
        var displayName = "Vídeo Selecionado"
        var sizeBytes = 0L

        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) {
                        displayName = cursor.getString(nameIndex) ?: displayName
                    }
                    if (sizeIndex != -1) {
                        sizeBytes = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (e: Exception) {
            displayName = uri.lastPathSegment ?: displayName
        }

        return VideoItem(
            id = System.currentTimeMillis(),
            uri = uri,
            title = displayName,
            sizeBytes = sizeBytes,
            isSample = false
        )
    }
}
