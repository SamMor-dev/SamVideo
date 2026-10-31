package com.example.player

import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout

/*
 * Copyright (c) 2026, Sam Moreno.
 * All rights reserved.
 * Licensed under the BSD 2-Clause License.
 */

/**
 * Modos de adaptação de proporção (Aspect Ratio) no SamVideo.
 */
@UnstableApi
enum class AspectRatioMode(val label: String, val resizeMode: Int) {
    /**
     * Ajusta o vídeo inteiro na tela preservando a proporção original (com barras pretas se necessário).
     */
    FIT("Ajustar", AspectRatioFrameLayout.RESIZE_MODE_FIT),

    /**
     * Aplica zoom cortando bordas discretamente para preencher 100% da tela moderna sem distorção.
     */
    ZOOM("Zoom", AspectRatioFrameLayout.RESIZE_MODE_ZOOM),

    /**
     * Estica o vídeo para preencher totalmente as dimensões do display.
     */
    FILL("Preencher", AspectRatioFrameLayout.RESIZE_MODE_FILL);

    fun next(): AspectRatioMode {
        val values = entries
        val nextIdx = (ordinal + 1) % values.size
        return values[nextIdx]
    }
}
