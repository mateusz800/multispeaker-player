package com.example.musicplayer.viewState

sealed class ScreenState {
    object Music: ScreenState()
    object Speakers: ScreenState()
    object Settings: ScreenState()
}