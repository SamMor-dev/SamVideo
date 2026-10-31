package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.data.VideoRepository
import com.example.model.VideoItem
import com.example.player.AspectRatioMode
import com.example.player.PlaybackUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/*
 * Copyright (c) 2026, Sam Moreno.
 * All rights reserved.
 * Licensed under the BSD 2-Clause License.
 */

/**
 * ViewModel reativo responsável pelo ciclo de vida do ExoPlayer,
 * garantindo aceleração por hardware pura (sem fallbacks pesados legados em C/C++)
 * e baixo consumo de energia para dispositivos modernos (Galaxy A, etc).
 */
@androidx.media3.common.util.UnstableApi
class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VideoRepository(application)

    // Instância única do ExoPlayer configurada para alta performance
    val player: ExoPlayer by lazy {
        initHardwareAcceleratedPlayer()
    }

    // Estado reativo da reprodução para o Jetpack Compose
    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()

    // Lista de vídeos locais escaneados do dispositivo
    private val _localVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    val localVideos: StateFlow<List<VideoItem>> = _localVideos.asStateFlow()

    // Lista de amostras de demonstração para teste rápido
    private val _sampleVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    val sampleVideos: StateFlow<List<VideoItem>> = _sampleVideos.asStateFlow()

    // Indicador de carregamento da lista
    private val _isLoadingVideos = MutableStateFlow(false)
    val isLoadingVideos: StateFlow<Boolean> = _isLoadingVideos.asStateFlow()

    // Filtro de busca na lista de vídeos
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Modo de tema visual (Escuro, Claro, Azul Sóbrio)
    private val _themeMode = MutableStateFlow(com.example.ui.theme.AppThemeMode.DARK)
    val themeMode: StateFlow<com.example.ui.theme.AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: com.example.ui.theme.AppThemeMode) {
        _themeMode.value = mode
    }

    // Job para polling suave de posição e buffer do vídeo
    private var progressTrackerJob: Job? = null
    // Job para auto-ocultação dos controles da interface após inatividade
    private var controlsTimeoutJob: Job? = null

    init {
        _sampleVideos.value = repository.getSampleVideos()
        loadVideos()
    }

    /**
     * Configura o ExoPlayer com decodificação direta por Hardware MediaCodec
     * desligando renderizadores de extensão lentos em software.
     */
    @androidx.media3.common.util.UnstableApi
    private fun initHardwareAcceleratedPlayer(): ExoPlayer {
        val context = getApplication<Application>()

        // Desativa decodificação lenta por software para forçar aceleração por GPU/VPU nativa
        val renderersFactory = DefaultRenderersFactory(context)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
            .setEnableDecoderFallback(true)

        // Configuração de fonte de dados HTTP com User-Agent padrão e suporte a redirecionamentos
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 SamVideo/1.0")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(20000)

        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        // Configuração de áudio ideal para filmes e vídeos
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        val exo = ExoPlayer.Builder(context, renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true)
            .setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()

        exo.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                _uiState.update { current ->
                    current.copy(
                        playbackState = playbackState,
                        durationMs = exo.duration.coerceAtLeast(0L),
                        currentPositionMs = exo.currentPosition.coerceAtLeast(0L),
                        bufferedPositionMs = exo.bufferedPosition.coerceAtLeast(0L),
                        errorMessage = if (playbackState == Player.STATE_IDLE && current.errorMessage != null) {
                            current.errorMessage
                        } else null
                    )
                }

                if (playbackState == Player.STATE_READY) {
                    startProgressTracker()
                } else if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                    stopProgressTracker()
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressTracker()
                    resetControlsTimeout()
                } else {
                    stopProgressTracker()
                    showControls()
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                _uiState.update {
                    it.copy(
                        videoWidth = videoSize.width,
                        videoHeight = videoSize.height
                    )
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Erro ao reproduzir vídeo: ${error.localizedMessage ?: "Formato não suportado"}",
                        isPlaying = false
                    )
                }
                stopProgressTracker()
            }
        })

        return exo
    }

    /**
     * Inicia a reprodução de um VideoItem especificado.
     */
    fun playVideo(video: VideoItem) {
        val mediaItem = MediaItem.fromUri(video.uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true

        _uiState.update {
            it.copy(
                videoTitle = video.title,
                errorMessage = null,
                currentPositionMs = 0L,
                durationMs = video.durationMs
            )
        }
        showControlsWithTimeout()
    }

    /**
     * Tenta recarregar e retomar o vídeo atual após erro de rede ou carregamento.
     */
    fun retry() {
        _uiState.update { it.copy(errorMessage = null) }
        player.prepare()
        player.playWhenReady = true
        showControlsWithTimeout()
    }

    /**
     * Alterna entre Play e Pause.
     */
    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_ENDED) {
                player.seekTo(0)
            }
            player.play()
        }
        resetControlsTimeout()
    }

    /**
     * Avança a reprodução no vídeo (padrão 10 segundos).
     */
    fun seekForward(deltaMs: Long = 10000L) {
        val target = (player.currentPosition + deltaMs).coerceAtMost(player.duration.coerceAtLeast(0L))
        player.seekTo(target)
        _uiState.update { it.copy(currentPositionMs = target) }
        resetControlsTimeout()
    }

    /**
     * Retrocede a reprodução no vídeo (padrão 10 segundos).
     */
    fun seekBackward(deltaMs: Long = 10000L) {
        val target = (player.currentPosition - deltaMs).coerceAtLeast(0L)
        player.seekTo(target)
        _uiState.update { it.copy(currentPositionMs = target) }
        resetControlsTimeout()
    }

    /**
     * Ajusta a posição de reprodução diretamente via Seekbar.
     */
    fun seekToPosition(positionMs: Long) {
        val target = positionMs.coerceIn(0L, player.duration.coerceAtLeast(0L))
        player.seekTo(target)
        _uiState.update { it.copy(currentPositionMs = target) }
        resetControlsTimeout()
    }

    /**
     * Ajusta a velocidade de reprodução (0.5x até 2.0x).
     */
    fun setPlaybackSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
        _uiState.update { it.copy(playbackSpeed = speed) }
        resetControlsTimeout()
    }

    /**
     * Alterna para o próximo modo de aspecto (FIT -> ZOOM -> FILL).
     */
    fun cycleAspectRatio() {
        val nextMode = _uiState.value.aspectRatioMode.next()
        _uiState.update { it.copy(aspectRatioMode = nextMode) }
        resetControlsTimeout()
    }

    /**
     * Alterna a visibilidade dos controles na tela.
     */
    fun toggleControlsVisibility() {
        val isNowVisible = !_uiState.value.isControlsVisible
        _uiState.update { it.copy(isControlsVisible = isNowVisible) }
        if (isNowVisible && player.isPlaying) {
            resetControlsTimeout()
        } else {
            controlsTimeoutJob?.cancel()
        }
    }

    /**
     * Força a exibição dos controles.
     */
    fun showControls() {
        _uiState.update { it.copy(isControlsVisible = true) }
        controlsTimeoutJob?.cancel()
    }

    /**
     * Mostra os controles com temporizador para auto-ocultação.
     */
    fun showControlsWithTimeout(timeoutMs: Long = 3500L) {
        _uiState.update { it.copy(isControlsVisible = true) }
        resetControlsTimeout(timeoutMs)
    }

    private fun resetControlsTimeout(timeoutMs: Long = 3500L) {
        controlsTimeoutJob?.cancel()
        controlsTimeoutJob = viewModelScope.launch {
            delay(timeoutMs)
            if (player.isPlaying) {
                _uiState.update { it.copy(isControlsVisible = false) }
            }
        }
    }

    /**
     * Alterna a trava de orientação retrato / paisagem.
     */
    fun togglePortraitLock() {
        _uiState.update { it.copy(isPortraitLocked = !it.isPortraitLocked) }
    }

    /**
     * Carrega a lista de vídeos locais do dispositivo.
     */
    fun loadVideos() {
        viewModelScope.launch {
            _isLoadingVideos.value = true
            val videos = repository.getLocalVideos()
            _localVideos.value = videos
            _isLoadingVideos.value = false
        }
    }

    /**
     * Atualiza o termo de pesquisa de vídeos.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    /**
     * Abre e reproduz um vídeo selecionado via SAF / File Picker externo.
     */
    fun openVideoUri(uri: Uri) {
        val videoItem = repository.resolveSelectedVideo(uri)
        playVideo(videoItem)
    }

    /**
     * Inicia o polling contínuo da posição e do buffer.
     */
    private fun startProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = viewModelScope.launch {
            while (isActive) {
                if (player.isPlaying) {
                    _uiState.update {
                        it.copy(
                            currentPositionMs = player.currentPosition.coerceAtLeast(0L),
                            bufferedPositionMs = player.bufferedPosition.coerceAtLeast(0L),
                            durationMs = player.duration.coerceAtLeast(0L)
                        )
                    }
                }
                delay(250L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressTracker()
        controlsTimeoutJob?.cancel()
        player.release()
    }
}
