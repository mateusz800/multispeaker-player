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
import net.joinu.rudp.RUDPSocket

import android.media.AudioTrack
import android.util.Log
import com.example.musicplayer.R
import kotlinx.coroutines.Dispatchers
import java.io.*
import java.lang.IndexOutOfBoundsException
import android.media.MediaFormat
import android.media.MediaCodec
import android.os.NetworkOnMainThreadException
import com.example.musicplayer.data.model.AudioModel
import com.example.musicplayer.data.repository.PlayerStateRepository
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.consumeAsFlow
import java.lang.Exception
import java.net.*
import java.nio.ByteBuffer


@AndroidEntryPoint
class PlayerService : Service() {

    enum class Action {
        CHANGE_TRACK,
        PLAY_PAUSE
    }

    enum class BroadcastParam {
        PATH, BITRATE
    }

    @Inject
    lateinit var deviceRepository: DeviceRepository

    @Inject
    lateinit var playerStateRepository: PlayerStateRepository

    private var playJob: Job? = null

    private var connectedDevices: List<DeviceModel> = listOf()

    private var bufferSize: Int = 512
    private lateinit var audioTrack: AudioTrack


    private val trackBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val path: Uri? = intent.getParcelableExtra(BroadcastParam.PATH.name)
            val bitrate: Int = intent.getIntExtra(BroadcastParam.BITRATE.name, 44100)
            if (path != null) startTrack(path, bitrate)
        }
    }

    private val playPauseBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.pause()
            } else {
                audioTrack.play()
            }
            playerStateRepository.togglePause()
        }
    }

    init {
        collectConnectedDevices()
        MainScope().launch(Dispatchers.IO) {
            startAudioServer()
        }
    }


    private fun initializeAudioTrack(bitrate: Int) {
        bufferSize = AudioTrack.getMinBufferSize(
            bitrate, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
            bufferSize = bitrate * 2;
        }
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(bitrate)
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

    private suspend fun startAudioServer() {

        while (true) {
            val serverSocket = RUDPSocket(8888)
            listenForPacketToReceive(serverSocket)

            // val client = serverSocket.accept()
            //val dataStream = client.getInputStream()
            initializeAudioTrack(bitrate = 44100)
            val buffer = ByteArray(bufferSize)
            var i: Int
            audioTrack.play()
            serverSocket.receiveQueue.consumeAsFlow().collect {
                val data: ByteBuffer = it.data
                audioTrack.write(data, 0, data.remaining())
            }

            /*dataStream.use {
                //val infoBuffer = ByteArray(2048)
                //dataStream.read(infoBuffer, 0, infoBuffer.size)
                //val jsonString = String(infoBuffer)
                //val audioModel = Gson().fromJson(jsonString, AudioModel::class.java)
                //playerStateRepository.updateCurrentTrack(audioModel)
                i = dataStream.read(buffer, 0, bufferSize)
                while (i > 0) {
                    audioTrack.write(buffer, 0, buffer.size)
                    i = dataStream.read(buffer, 0, bufferSize)
                }
            }
             */


        }
    }

    private suspend fun listenForPacketToReceive(socket: RUDPSocket) {
        while (true) {
            MainScope().launch(Dispatchers.IO) {
                socket.receive()
            }
        }
    }


    private fun startPlaying(trackPath: Uri, bitrate: Int) {
        initializeAudioTrack(bitrate)
        playJob = MainScope().launch(Dispatchers.IO) {
            var socket: RUDPSocket? = null
            var host: InetSocketAddress? = null
            try {
                if (connectedDevices.isNotEmpty()) {
                    socket = RUDPSocket()
                    //socket.setPerformancePreferences(1, 0, 0)
                    //socket.bind(null)
                    val ipAddress = connectedDevices[0].ipAddress
                    //Log.d("PlayerService", "Connecting to $host:8888")
                    host = InetSocketAddress(ipAddress, 8888)
                    socket.bind(host)
                    //Log.d("PlayerService", "Connected to $host : ${socket.isConnected}")
                }
            } catch (e: Exception) {
                Log.d("PlayerService", "Cannot connect to host - ${e.localizedMessage}")
            }
            val stream = contentResolver.openInputStream(trackPath)
            val dataStream = DataInputStream(stream)
            val buffer = ByteArray(bufferSize)
            var i: Int
            playerStateRepository.turnOffPause()
            audioTrack.play()
            dataStream.use {
                // var dos =
                //if (socket != null && socket.isConnected) DataOutputStream(socket.getOutputStream()) else null
                /*
                 val jsonString:String = Gson().toJson(playerStateRepository.currentTrack.value)

                 val infoByteArray:ByteArray = jsonString.byteInputStream().readBytes()
                 // TODO: do something with handwritten buffer size
                 val sizeByteArray = ByteArray(infoByteArray.size)
                 dos?.write(sizeByteArray, 0, sizeByteArray.size)
                 dos?.write(infoByteArray, 0 , infoByteArray.size)

                 */
                i = dataStream.read(buffer, 0, bufferSize)
                while (i > 0 && playJob!!.isActive) {
                    if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                        audioTrack.write(buffer, 0, buffer.size)
                        if (host != null) {
                            socket?.send(ByteBuffer.wrap(buffer), host)
                        }
                        i = dataStream.read(buffer, 0, bufferSize)
                    }
                }
                socket?.close()
            }
            if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.stop()
            }
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

    private fun startTrack(trackPath: Uri, bitrate: Int) {
        stopCurrentTrack()
        startPlaying(trackPath, bitrate)
    }

    private fun stopCurrentTrack() {
        playJob?.cancel()
        if (this::audioTrack.isInitialized) {
            audioTrack.pause()
            audioTrack.flush()
            audioTrack.release()
        }
    }
}