package com.example.ui.theme

/*
 * Copyright (c) 2026, Sam Moreno.
 * Licensed under the BSD 2-Clause License.
 */

/**
 * Modos de tema visual refinados do SamVideo:
 * - ESCURO: Preto carvão neutro e sóbrio para baixo consumo de bateria.
 * - CLARO: Superfícies limpas e minimalistas em off-white.
 * - AZUL SÓBRIO: Tons profundos de azul ardósia sem gradientes.
 */
enum class AppThemeMode(
    val title: String,
    val subtitle: String
) {
    DARK("Escuro", "Carvão neutro e sóbrio"),
    LIGHT("Claro", "Minimalista e alto contraste"),
    SLATE_BLUE("Azul Sóbrio", "Azul ardósia profundo")
}
