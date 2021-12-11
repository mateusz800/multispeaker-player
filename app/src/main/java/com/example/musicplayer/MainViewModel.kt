package com.example.musicplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.viewState.ScreenState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val mainIntent = Channel<MainIntent>(Channel.UNLIMITED)

    private val _currentScreenState = MutableLiveData<ScreenState>(ScreenState.Music)
    val currentScreenState: LiveData<ScreenState>
        get() = _currentScreenState

    init {
        handleIntent()
    }

    private fun handleIntent(){
        viewModelScope.launch {
            mainIntent.consumeAsFlow().collect {
                when(it){
                    is MainIntent.NavigateToMusic -> _currentScreenState.postValue(ScreenState.Music)
                    is MainIntent.NavigateToSpeakers -> _currentScreenState.postValue(ScreenState.Speakers)
                }
            }
        }
    }
}