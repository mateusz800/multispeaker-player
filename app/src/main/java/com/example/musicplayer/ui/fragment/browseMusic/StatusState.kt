package com.example.musicplayer.ui.fragment.browseMusic

sealed class StatusState {
    object Loading: StatusState()
    object Loaded: StatusState()
    object Error: StatusState()
}