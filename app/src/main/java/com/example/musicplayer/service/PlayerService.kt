package com.example.musicplayer.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.*
import android.net.Uri
import android.os.IBinder
import com.example.musicplayer.data.model.DeviceModel
import com.example.musicplayer.data.repository.DeviceRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

import android.media.AudioTrack
import android.util.Log
import com.example.musicplayer.R
import kotlinx.coroutines.Dispatchers
import java.io.*
import java.lang.IndexOutOfBoundsException
import java.net.URL
import android.media.MediaFormat
import android.media.MediaCodec
import java.nio.ByteBuffer


@AndroidEntryPoint
class PlayerService : Service() {

    enum class Action {
        CHANGE_TRACK,
        PLAY_PAUSE
    }

    enum class BroadcastParam {
        PATH
    }

    @Inject
    lateinit var deviceRepository: DeviceRepository


    private var connectedDevices: List<DeviceModel> = listOf()

    //private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var currentMediaSourcePath: String? = null

    private val trackBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val path = intent.getStringExtra(BroadcastParam.PATH.name)
            if (path != null) startTrack(path)
        }
    }

    private val playPauseBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            /*
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }

             */
        }
    }

    init {
       // mediaPlayer.setOnPreparedListener { startPlaying() }
        collectConnectedDevices()
    }



    private fun startPlaying() {
        //mediaPlayer.start()
        //val streamer = AudioSender(this, connectedDevices[0].host, 8888)
        //streamer.stream(currentMediaSourcePath!!)
        val sampleRate = 44100
        var bufferSize = AudioTrack.getMinBufferSize(
            sampleRate, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
            bufferSize = sampleRate * 2;
        }
        // AudioTrack needs the buffer size in bytes (Short uses 2 bytes)
        bufferSize *= 2
        val audioTrack: AudioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_DEFAULT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setBufferSizeInBytes(bufferSize*2)
            .build()
        if(audioTrack.state == AudioTrack.STATE_UNINITIALIZED){
            Log.e("PlayerService", "Audio track uninitialized")
        }


        val stream = URL("https://file-examples-com.github.io/uploads/2017/11/file_example_WAV_10MG.wav").openStream()
        //val stream = resources.openRawResource(R.raw.sample)
        //val stream = FileInputStream(currentMediaSourcePath)
        val bufferedInputStream = BufferedInputStream(stream)
        val dataStream = TTSInputStream(bufferedInputStream)
        //val dataStream = DataInputStream(bufferedInputStream)
        //val bufferSize = 512
        val buffer = ByteArray(bufferSize)
        var i = 0
        audioTrack.play()
        dataStream.use {
            i = dataStream.read(buffer, 0, bufferSize)
            while (i > 0) {
                audioTrack.write(buffer, 0, buffer.size)
                i = dataStream.read(buffer, 0, bufferSize)
            }
        }
        audioTrack.stop()
        audioTrack.release()
    }

    private fun collectConnectedDevices() {
        MainScope().launch {
            deviceRepository.connectedDevices.collect {
                if (it != null) {
                    connectedDevices = it
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(trackBroadcastReceiver, IntentFilter(Action.CHANGE_TRACK.name))
        registerReceiver(playPauseBroadcastReceiver, IntentFilter(Action.PLAY_PAUSE.name))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        //mediaPlayer.release()
    }

    private fun startTrack(trackPath: String) {
       //mediaPlayer.reset()
        currentMediaSourcePath = trackPath
        //mediaPlayer.setDataSource(this, Uri.parse(trackPath))
        //mediaPlayer.prepareAsync()
        MainScope().launch(Dispatchers.IO) {
            startPlaying()
        }
    }
}