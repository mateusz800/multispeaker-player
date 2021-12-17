package com.example.musicplayer.ui.fragment.browseMusic

import com.example.musicplayer.data.model.AudioModel

sealed class MusicIntent {
    object FetchAudio: MusicIntent()
    class Play(val track: AudioModel): MusicIntent()
}