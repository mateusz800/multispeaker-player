package com.example.musicplayer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.ui.main.viewState.ScreenState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val mainIntent = Channel<MainIntent>(Channel.UNLIMITED)

    private val _currentScreenState = MutableStateFlow<ScreenState>(ScreenState.Music)
    val currentScreenState: StateFlow<ScreenState>
        get() = _currentScreenState

    init {
        handleIntent()
    }

    private fun handleIntent(){
        viewModelScope.launch {
            mainIntent.consumeAsFlow().collect {
                when(it){
                    is MainIntent.NavigateToMusic -> _currentScreenState.emit(ScreenState.Music)
                    is MainIntent.NavigateToSpeakers -> _currentScreenState.emit(ScreenState.Speakers)
                }
            }
        }
    }
}