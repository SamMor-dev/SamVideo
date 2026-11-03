package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Copyright (c) 2026, Sam Moreno.
 * All rights reserved.
 * Licensed under the BSD 2-Clause License.
 */

data class OpenSourceLibrary(
    val name: String,
    val author: String,
    val license: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenSourceLicensesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val libraries = listOf(
        OpenSourceLibrary("AndroidX Media3 (ExoPlayer)", "Google / AndroidX", "Apache License 2.0", "Mecanismo de reprodução de mídia de alta performance e aceleração por hardware."),
        OpenSourceLibrary("Jetpack Compose", "Google / AndroidX", "Apache License 2.0", "Toolkit moderno nativo para construção de interfaces declarativas em Android."),
        OpenSourceLibrary("Material 3 (Compose Material)", "Google / AndroidX", "Apache License 2.0", "Sistema de design Material 3 para Jetpack Compose."),
        OpenSourceLibrary("Kotlin Coroutines", "JetBrains", "Apache License 2.0", "Biblioteca para programação assíncrona e multithreading em Kotlin."),
        OpenSourceLibrary("AndroidX Lifecycle & ViewModel", "Google / AndroidX", "Apache License 2.0", "Gerenciamento de ciclo de vida e retenção de estado de UI."),
        OpenSourceLibrary("AndroidX Room", "Google / AndroidX", "Apache License 2.0", "Biblioteca de persistência de banco de dados SQLite sobre arquitetura robusta."),
        OpenSourceLibrary("Retrofit & OkHttp", "Square, Inc.", "Apache License 2.0", "Clientes HTTP seguros e tipados para consumo de APIs REST."),
        OpenSourceLibrary("Moshi", "Square, Inc.", "Apache License 2.0", "Biblioteca JSON moderna para Java e Kotlin."),
        OpenSourceLibrary("Firebase Android BoM", "Google / Firebase", "Apache License 2.0", "Ecossistema de serviços em nuvem, autenticação e banco de dados."),
        OpenSourceLibrary("AndroidX Credential Manager", "Google / AndroidX", "Apache License 2.0", "Gerenciamento unificado de credenciais e autenticação segura."),
        OpenSourceLibrary("AboutLibraries", "Mike Penz", "Apache License 2.0", "Exibição de licenças de código aberto e dependências de projetos Android.")
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Licenças de Código Aberto",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(libraries) { lib ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = lib.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Por ${lib.author} • ${lib.license}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                        )
                        Text(
                            text = lib.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
