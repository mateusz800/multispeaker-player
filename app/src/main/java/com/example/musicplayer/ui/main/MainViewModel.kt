package com.example.musicplayer.ui.main

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.AudioModel
import com.example.musicplayer.data.repository.AudioRepository
import com.example.musicplayer.service.PlayerService
import com.example.musicplayer.ui.domain.BaseViewModel
import com.example.musicplayer.ui.main.viewState.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val audioRepository: AudioRepository
) : BaseViewModel(application) {
    val mainIntent = Channel<MainIntent>(Channel.UNLIMITED)

    private val _currentScreenState = MutableStateFlow<ScreenState>(ScreenState.Music)
    val currentScreenState: StateFlow<ScreenState>
        get() = _currentScreenState

    private val _currentTrack = MutableLiveData<AudioModel>()
    val currentTrack: LiveData<AudioModel>
        get() = _currentTrack

    init {
        handleIntent()
        handlePlayerChanges()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            mainIntent.consumeAsFlow().collect {
                when (it) {
                    is MainIntent.NavigateToMusic -> _currentScreenState.emit(ScreenState.Music)
                    is MainIntent.NavigateToSpeakers -> _currentScreenState.emit(ScreenState.Speakers)
                    is MainIntent.NavigateToSettings -> TODO()
                    MainIntent.Pause -> stopPlaying()
                }
            }
        }
    }

    private fun handlePlayerChanges() {
        viewModelScope.launch {
            audioRepository.recentTrack.collect {
                if (it != null) {
                    _currentTrack.postValue(it)
                }
            }
        }
    }

    private fun stopPlaying(){
        context.sendBroadcast(Intent(PlayerService.Action.PLAY_PAUSE.name))
    }

}