# SamVideo 🎬⚡

[![License](https://img.shields.io/badge/License-BSD_2--Clause-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84.svg?logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple.svg?logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Media3](https://img.shields.io/badge/Engine-AndroidX%20Media3%20ExoPlayer-brightgreen.svg)](https://developer.android.com/media/media3)

**SamVideo** é um reprodutor de vídeo Android leve, modular e focado em altíssima performance. Desenvolvido em **Kotlin** puro com interface moderna em **Jetpack Compose (Material Design 3)** e motor de reprodução alimentado pela **AndroidX Media3 (ExoPlayer)**.

Projetado com uma interface executiva de alto contraste e aceleração de hardware por cópia zero (zero-copy), o SamVideo oferece inicialização instantânea, consumo mínimo de bateria e reprodução fluida para vídeos locais do dispositivo e integrações com o SAF (Storage Access Framework).

> **Autor:** Sam Moreno  
> **Licença:** BSD 2-Clause License

---

## 👩‍💻 Bem-vindo, Desenvolvedor! (Guia de Início Rápido)

Novo no código? Aqui está tudo o que você precisa para começar em poucos minutos.

### Pré-requisitos
* **Android Studio:** Ladybug / Koala ou mais recente.
* **JDK:** Java 17 ou superior.
* **Android SDK:** Compile SDK 36, Min SDK 24 (Android 7.0+).

### 1. Clonar e Abrir
```bash
git clone https://github.com/SamMor-dev/SamVideo.git
```
1. Abra o **Android Studio**.
2. Selecione **File > Open** e escolha a pasta `SamVideo`.
3. Aguarde a sincronização automática do Gradle.

### 2. Executar o Aplicativo
1. Conecte um dispositivo Android com a **Depuração USB** ativada ou inicie um Emulador Android (API 24+).
2. Clique no botão verde **Run (`▶`)** na barra de ferramentas do Android Studio (ou pressione `Shift + F10`).

### 3. Executar Testes Unitários
Para verificar a integridade da suíte de testes:
```bash
./gradlew testDebugUnitTest
```

---

## 🚀 Destaques Técnicos e Arquitetura

1. **Aceleração por Hardware Pura (Zero-Copy):**
   - Configurado com `DefaultRenderersFactory` desabilitando renderizadores lentos em software por extensão (`EXTENSION_RENDERER_MODE_OFF`).
   - Uso direto de decodificadores de hardware nativos da GPU/VPU via `MediaCodec` e `SurfaceView` overlay para máximo framerate e consumo mínimo de bateria.

2. **MVVM Moderno & UI Reativa:**
   - Construído inteiramente com **Jetpack Compose** e **Kotlin Coroutines / StateFlow**.
   - `PlayerViewModel` desacoplado que gerencia o ciclo de vida do player, estado de reprodução, navegação (seeking), controle de velocidade e temas.

3. **Principais Recursos:**
   - **MediaStore & SAF:** Indexa automaticamente vídeos locais do dispositivo e suporta a abertura de qualquer arquivo (`MP4`, `MKV`, `WebM`, `3GP`, `TS`) via seletor de arquivos do sistema.
   - **HUD Imersivo & Gestos Intuitivos:**
     - Toque duplo na esquerda/direita: retrocede ou avança 10s.
     - Deslizar verticalmente (esquerda): ajusta o brilho da tela.
     - Deslizar verticalmente (direita): ajusta o volume do áudio.
   - **Modos de Aspecto (Aspect Ratio):** Ajustar (Fit), Zoom e Preencher (Fill).
   - **Picture-in-Picture (PiP):** Suporte nativo ao modo janela flutuante.
   - **Seletor de Temas:** Paletas Escuro (preto AMOLED), Claro e Azul Sóbrio (Slate Blue).

---

## 📂 Estrutura de Diretórios do Código

```
app/src/main/java/com/example/
├── MainActivity.kt                # Ponto de entrada, tema reativo, PiP e intents externos
├── data/
│   └── VideoRepository.kt        # Consulta ao MediaStore e resolvedor de URIs do SAF
├── model/
│   └── VideoItem.kt              # Modelo de dados de vídeo (duração, tamanho, resolução)
├── player/
│   ├── AspectRatioMode.kt        # Modos de aspecto (FIT, ZOOM, FILL)
│   └── PlaybackState.kt          # Estado reativo do player
├── ui/
│   ├── components/
│   │   ├── AboutDialog.kt             # Diálogo modal com Autor (Sam Moreno) e Licença BSD 2-Clause
│   │   ├── GestureIndicatorOverlay.kt  # Feedback visual de gestos (volume, brilho, seek)
│   │   ├── PlayerControlsOverlay.kt    # HUD de controles (Seekbar, aspect ratio, PiP, velocidade)
│   │   ├── ThemeSelectionDialog.kt     # Seletor de temas (Escuro, Claro, Azul Sóbrio)
│   │   └── VideoPlayerView.kt          # Encapsulamento de SurfaceView acelerado por hardware
│   ├── screens/
│   │   ├── PlayerScreen.kt             # Tela de reprodução imersiva com BackHandler
│   │   └── VideoListScreen.kt          # Seletor limpo de vídeos com busca e seletor de arquivos
│   └── theme/
│       ├── Color.kt                    # Cores sólidas sem gradientes
│       ├── Theme.kt                    # Configuração de tema Material Design 3 e insets
│       ├── ThemeMode.kt                # Enum com os modos de aparência visual
│       └── Type.kt                     # Tipografia Material 3
└── viewmodel/
    └── PlayerViewModel.kt        # Gerenciamento reativo do ciclo de vida e estado do player
```

---

## 📜 Licença

Distribuído sob a licença **BSD 2-Clause**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

```
Copyright (c) 2026, Sam Moreno. All rights reserved.
```
