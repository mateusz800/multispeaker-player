package com.example.musicplayer.service

import android.content.Context
import android.net.Uri
import java.io.DataOutputStream
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket

class AudioSender(private val context: Context, host: String, port:Int) {
    private val socket = Socket()
    private val buf = ByteArray(1024)
    private var len: Int = 0

    init {
        socket.bind(null)
        socket.connect((InetSocketAddress(host, port)), 500)
    }

    fun stream(filePath:String){
        //val outputStream = socket.getOutputStream()
        val dos = DataOutputStream(socket.getOutputStream())

        val cr = context.contentResolver
        val inputStream: InputStream = cr.openInputStream(Uri.parse(filePath))!!
        while (inputStream.read(buf).also { len = it } != -1) {
            dos.write(buf, 0, len)
        }
        dos.close()
        inputStream.close()
    }

}