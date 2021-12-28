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
import android.media.MediaFormat
import android.media.MediaCodec
import android.os.NetworkOnMainThreadException
import java.net.*
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

    private val sampleRate = 44100
    private var bufferSize: Int = 512
    private lateinit var audioTrack: AudioTrack

    private var currentMediaSourcePath: String? = null

    private val trackBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val path = intent.getStringExtra(BroadcastParam.PATH.name)
            if (path != null) startTrack(path)
        }
    }

    private val playPauseBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

        }
    }

    init {
        collectConnectedDevices()
        initializeAudioTrack()
        MainScope().launch(Dispatchers.IO) {
            startAudioServer()
        }
    }

    private fun initializeAudioTrack() {
        bufferSize = AudioTrack.getMinBufferSize(
            sampleRate, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
            bufferSize = sampleRate * 2;
        }
        // AudioTrack needs the buffer size in bytes (Short uses 2 bytes)
        //bufferSize *= 2
        audioTrack = AudioTrack.Builder()
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
            .setBufferSizeInBytes(bufferSize * 2)
            .build()
        if (audioTrack.state == AudioTrack.STATE_UNINITIALIZED) {
            Log.e("PlayerService", "Audio track uninitialized")
        }
    }

    private fun startAudioServer() {
        val serverSocket = ServerSocket(8888)
        return serverSocket.use {
            val client = serverSocket.accept()
            val dataStream = client.getInputStream()
            val buffer = ByteArray(bufferSize)
            var i: Int
            audioTrack.play()
            dataStream.use {
                i = dataStream.read(buffer, 0, bufferSize)
                while (i > 0) {
                    audioTrack.write(buffer, 0, buffer.size)
                    i = dataStream.read(buffer, 0, bufferSize)
                }
            }
            audioTrack.stop()
        }
    }


    private fun startPlaying() {
        //val streamer = AudioSender(this, connectedDevices[0].host, 8888)
        MainScope().launch(Dispatchers.IO) {
            val stream = resources.openRawResource(R.raw.sample)
            var socket: Socket? = null

            try {
                if (connectedDevices.isNotEmpty()) {
                    socket = Socket()
                    //socket.setPerformancePreferences(1, 0, 0)
                    socket.bind(null)
                    val host = connectedDevices[0].ipAddress
                    Log.d("PlayerService", "Connecting to $host:8888")
                    socket.connect((InetSocketAddress(host, 8888)), 500)
                    Log.d("PlayerService", "Connected to $host : ${socket.isConnected}")
                }
            } catch (e: UnknownHostException) {
                Log.d("PlayerService", "Cannot connect to host - unknown host")
            } catch (e: NetworkOnMainThreadException) {
                Log.d("PlayerService", "Cannot connect to host - network exception")
            } catch (e: ConnectException) {
                Log.d("PlayerService", "Cannot connect to host - connection refused")
            }

            //val stream = URL("https://file-examples-com.github.io/uploads/2017/11/file_example_WAV_10MG.wav").openStream()

            //val stream = FileInputStream(currentMediaSourcePath)
            val dataStream = DataInputStream(stream)
            val buffer = ByteArray(bufferSize)
            var i: Int
            audioTrack.play()
            dataStream.use {
                val dos =
                    if (socket != null && socket.isConnected) DataOutputStream(socket.getOutputStream()) else null
                i = dataStream.read(buffer, 0, bufferSize)
                while (i > 0) {
                    audioTrack.write(buffer, 0, buffer.size)
                    dos?.write(buffer, 0, buffer.size)
                    i = dataStream.read(buffer, 0, bufferSize)

                }
                dos?.close()
            }
            audioTrack.stop()
            audioTrack.release()
        }

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

    private fun startTrack(trackPath: String) {
        currentMediaSourcePath = trackPath

        startPlaying()

    }
}