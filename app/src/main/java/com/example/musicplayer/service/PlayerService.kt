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
        CHANGE_TRACK,
        PLAY_PAUSE
    }

    enum class BroadcastParam {
        PATH
    }

    private var mediaPlayer: MediaPlayer? = null

    private val trackBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val path = intent.getStringExtra(BroadcastParam.PATH.name)
            if (path != null) play(path)
        }
    }

    private val playPauseBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(trackBroadcastReceiver, IntentFilter(Action.CHANGE_TRACK.name))
        registerReceiver(playPauseBroadcastReceiver, IntentFilter(Action.PLAY_PAUSE.name) )
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