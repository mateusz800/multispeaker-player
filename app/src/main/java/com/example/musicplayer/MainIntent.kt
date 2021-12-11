package com.example.musicplayer

sealed class MainIntent {
    object NavigateToMusic: MainIntent()
    object NavigateToSpeakers: MainIntent()
    object NavigateToSettings: MainIntent()
}