package com.example.musicplayer.service

import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.IndexOutOfBoundsException

class TTSInputStream(`in`: InputStream?) : DataInputStream(`in`) {
    @JvmOverloads
    @Throws(IOException::class)
    fun readFullyUntilEof(b: ByteArray, off: Int = 0, len: Int = b.size): Int {
        if (len < 0) throw IndexOutOfBoundsException()
        var n = 0
        while (n < len) {
            val count: Int = `in`.read(b, off + n, len - n)
            if (count < 0) break
            n += count
        }
        return n
    }
}
