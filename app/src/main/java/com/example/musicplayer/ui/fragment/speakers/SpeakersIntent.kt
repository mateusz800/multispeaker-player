package com.example.musicplayer.ui.fragment.speakers

sealed class SpeakersIntent {
    class Connect(val host:String): SpeakersIntent()
}