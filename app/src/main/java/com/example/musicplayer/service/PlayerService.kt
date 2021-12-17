package com.example.musicplayer.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerService : Service() {
    enum class Action {
        CHANGE_TRACK
    }

    enum class BroadcastParam {
        PATH
    }

    private var mediaPlayer: MediaPlayer? = null

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val path = intent.getStringExtra(BroadcastParam.PATH.name)
            if (path != null) play(path)
        }
    }

    override fun onCreate() {
        super.onCreate()
        // TODO: remove hardcoding action
        registerReceiver(broadcastReceiver, IntentFilter(Action.CHANGE_TRACK.name))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    fun play(trackPath: String) {
        mediaPlayer?.stop()
        mediaPlayer = MediaPlayer.create(this, Uri.parse(trackPath))
        mediaPlayer!!.start()
    }
}