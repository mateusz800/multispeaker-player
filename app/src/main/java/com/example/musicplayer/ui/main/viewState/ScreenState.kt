package com.example.musicplayer.ui.main.viewState

sealed class ScreenState {
    object Music: ScreenState()
    object Speakers: ScreenState()
    object Settings: ScreenState()
}