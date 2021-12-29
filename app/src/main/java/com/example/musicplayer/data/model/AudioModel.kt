package com.example.musicplayer.data.model

import android.net.Uri

data class AudioModel(
    val path: Uri,
    val name: String,
    val artist: String,
    val bitrate: Int = 44100
)