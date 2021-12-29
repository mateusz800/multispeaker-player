package com.example.musicplayer.data.repository

import com.example.musicplayer.data.model.AudioModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerStateRepository {
    private val _currentTrack = MutableStateFlow<AudioModel?>(null)
    val currentTrack: StateFlow<AudioModel?>
        get() = _currentTrack

    private val _pauseState = MutableStateFlow(false)
    val pauseState: StateFlow<Boolean>
        get() = _pauseState

    fun updateCurrentTrack(model: AudioModel){
        MainScope().launch {
            _currentTrack.emit(model)
        }
    }

    fun togglePause(){
        MainScope().launch {
            _pauseState.emit(!(pauseState.value))
        }
    }


}