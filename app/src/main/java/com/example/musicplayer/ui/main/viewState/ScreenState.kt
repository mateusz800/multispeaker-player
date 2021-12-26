package com.example.musicplayer.ui.main.viewState

sealed class ScreenState(val value:String) {
    object Music: ScreenState("Music")
    object Speakers: ScreenState("Speakers")
    object Settings: ScreenState("Settings")
}