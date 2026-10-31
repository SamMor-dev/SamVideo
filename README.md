# SamVideo 🎬⚡

[![License](https://img.shields.io/badge/License-BSD_2--Clause-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84.svg?logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple.svg?logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Media3](https://img.shields.io/badge/Engine-AndroidX%20Media3%20ExoPlayer-brightgreen.svg)](https://developer.android.com/media/media3)

**SamVideo** é um reprodutor de vídeo Android leve, modular e focado em altíssima performance. Desenvolvido em **Kotlin** puro com interface moderna em **Jetpack Compose (Material Design 3)** e motor de reprodução alimentado pela **AndroidX Media3 (ExoPlayer)**.

> **Autor:** Sam Moreno  
> **Licença:** BSD 2-Clause License

---

## 🎨 Design Sóbrio, Refinado & Ícone SVG Editável

1. **Design Sóbrio & Sem Gradientes:**
   - Visual executivo com cores sólidas e alto contraste.
   - **Seletor de Aparência Integrado:**
     - **Escuro (Dark):** Preto carvão neutro e sóbrio para máxima eficiência energética em telas AMOLED.
     - **Claro (Light):** Superfícies limpas e minimalistas em off-white.
     - **Azul Sóbrio (Slate Blue):** Tons profundos de azul ardósia sem gradientes.

2. **Ícone S Vetorial em SVG:**
   - Localizado em `sam_video_icon.svg` e `art/sam_video_icon.svg`.
   - Projetado em camadas limpas com a letra **S** integrada a perfurações de película e um botão de Play de vídeo centralizado.
   - 100% pronto para ser aberto e editado no **Figma, Adobe Illustrator, Inkscape ou navegadores** por qualquer pessoa ou empresa.

3. **Seção 'Sobre' com Autor e Licença no Canto da Tela:**
   - Botão de informações `(i)` no canto superior da tela inicial.
   - Exibe o autor (**Sam Moreno**), a **Licença BSD 2-Clause** completa e especificações técnicas de decodificação e aceleração por hardware.

---

## 🚀 Destaques Técnicos e Performance

1. **Aceleração por Hardware Pura (Zero-Copy):**
   - Configurado com `DefaultRenderersFactory` desabilitando renderizadores lentos em software por extensão (`EXTENSION_RENDERER_MODE_OFF`).
   - Uso direto dos decodificadores de hardware nativos da GPU/VPU (`MediaCodec`).
   - Exibição de vídeo desacoplada via `SurfaceView`, permitindo overlay de hardware direto para máximo framerate e consumo mínimo de bateria — ideal para uso prolongado em aparelhos como a linha **Samsung Galaxy A**, Motorola, Xiaomi e Pixel.

2. **Código Desacoplado & Leve:**
   - Livre de bibliotecas legadas pesadas em C/C++ ou runtimes desnecessários.
   - Inicialização instantânea (< 150ms).
   - Otimizado para manter o APK extremamente compacto.

3. **Recursos de Interface (UX Moderna):**
   - **Navegador de Vídeos Nativo:** Escaneia vídeos locais do MediaStore e oferece botão de abertura direta de qualquer arquivo (`MP4`, `MKV`, `WebM`, `3GP`, `TS`).
   - **Controles em Tela Cheia:** HUD com Play/Pause, barra de progresso suave (Seekbar), indicador de tempo decorrido e restante.
   - **Gestos Intuitivos:**
     - Toque duplo na esquerda: retrocede 10s.
     - Toque duplo na direita: avança 10s.
     - Deslizar verticalmente na lateral esquerda: ajusta o brilho da tela.
     - Deslizar verticalmente na lateral direita: ajusta o volume do áudio.
   - **Adaptação de Aspecto (Aspect Ratio):**
     - **Ajustar (FIT):** Preserva proporção original com barras pretas.
     - **Zoom (ZOOM):** Corta bordas suavemente preenchendo 100% de displays modernos 19.5:9 e 20:9.
     - **Preencher (FILL):** Estica para ocupar a tela toda.
   - **Controle de Rotação de Tela:** Alternância rápida entre retrato e paisagem sem reiniciar o player (mantém estado através de `configChanges`).
   - **Velocidade de Reprodução:** 0.5x, 0.75x, 1.0x, 1.25x, 1.5x e 2.0x com preservação do pitch de áudio.
   - **Picture-in-Picture (PiP):** Suporte nativo ao modo janela flutuante ao sair do app.
   - **Integração com o Sistema:** Abre vídeos diretamente de outros apps (gerenciador de arquivos, mensageiros, etc.) via intent `ACTION_VIEW`.

---

## 🛠️ Como Compilar e Executar no Android Studio

### Passo 1: Instalação Rápida do Android Studio (Linux)
Se estiver em uma distribuição Linux (Ubuntu, Debian, Fedora, Arch), você pode usar o script automatizado fornecido na raiz do projeto:

```bash
chmod +x install_android_studio.sh
./install_android_studio.sh
```
*O script verificará o Java 17, instalará dependências de sistema e configurará o Android Studio no seu sistema.*

*(No Windows ou macOS, basta baixar e instalar a versão estável mais recente em [developer.android.com/studio](https://developer.android.com/studio)).*

---

### Passo 2: Abrir o Projeto no Android Studio
1. Abra o **Android Studio**.
2. Clique em **File > Open...** (ou na tela de boas-vindas, selecione **Open**).
3. Navegue até a pasta do projeto `SamVideo` e clique em **OK**.
4. A sincronização do Gradle ocorrerá rapidamente graças às otimizações de cache e `parallel=true` configuradas em `gradle.properties`.

---

### Passo 3: Executar no Dispositivo ou Emulador
1. Conecte seu dispositivo Android com a **Depuração USB** ativada (ou inicie um emulador Android via Virtual Device Manager).
2. Selecione o dispositivo na barra de ferramentas superior.
3. Clique no botão verde de **Run** `▶` (ou pressione `Shift + F10`).
4. O app será compilado, instalado e aberto automaticamente.

---

### Passo 4: Gerar o APK de Instalação Direta
Para gerar o arquivo `.apk` final para instalar manualmente em qualquer aparelho:
1. No menu superior do Android Studio, clique em:  
   **Build > Build Bundle(s) / APK(s) > Build APK(s)**
2. Após o término, clique na notificação **"locate"** para abrir o arquivo gerado em:  
   `app/build/outputs/apk/debug/app-debug.apk`

---

## 📂 Arquitetura Modular do Código

```
app/src/main/java/com/example/
├── MainActivity.kt                # Ponto de entrada, tema reativo, PiP e intents externos
├── data/
│   └── VideoRepository.kt        # Consulta ao MediaStore e resolvedor de URIs do SAF
├── model/
│   └── VideoItem.kt              # Modelo de dados com cálculo de duração e resolução
├── player/
│   ├── AspectRatioMode.kt        # Modos de aspecto (FIT, ZOOM, FILL)
│   └── PlaybackState.kt          # Estado reativo do player (posição, buffer, velocidade)
├── ui/
│   ├── components/
│   │   ├── AboutDialog.kt             # Diálogo modal com Autor (Sam Moreno) e Licença BSD 2-Clause
│   │   ├── GestureIndicatorOverlay.kt  # Feedback visual de gestos (volume, brilho, seek)
│   │   ├── PlayerControlsOverlay.kt    # HUD de controles com Seekbar, Aspect Ratio e PiP
│   │   ├── ThemeSelectionDialog.kt     # Seletor de temas (Escuro, Claro, Azul Sóbrio)
│   │   └── VideoPlayerView.kt          # Encapsulamento de SurfaceView acelerado por hardware
│   ├── screens/
│   │   ├── PlayerScreen.kt             # Tela de reprodução imersiva com BackHandler
│   │   └── VideoListScreen.kt          # Seletor limpo de vídeos com busca, filtros e botão About
│   └── theme/
│       ├── Color.kt                    # Paletas sólidas sem gradientes (Dark, Light, Slate Blue)
│       ├── Theme.kt                    # Configuração de tema Material Design 3 e insets
│       └── ThemeMode.kt                # Enum com os 3 modos de aparência visual
sam_video_icon.svg                     # Ícone vetorial oficial editável (SVG puro)
art/sam_video_icon.svg                 # Cópia do ícone vetorial na pasta de arte
```

---

## 📜 Licença

Distribuído sob a licença **BSD 2-Clause**. Veja o arquivo [LICENSE](LICENSE) para obter mais informações.

```
Copyright (c) 2026, Sam Moreno. All rights reserved.
```
