package com.example.musicplayer.ui.main

sealed class MainIntent {
    object NavigateToMusic: MainIntent()
    object NavigateToSpeakers: MainIntent()
    object NavigateToSettings: MainIntent()

    object Pause:MainIntent()
}