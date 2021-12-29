package com.example.musicplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.AudioTrack
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.musicplayer.data.model.AudioModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.Flow
import android.webkit.MimeTypeMap




class AudioRepository(private val context: Context) {



    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAllAudioFromDevice(): List<AudioModel> {
        val tempAudioList: MutableList<AudioModel> = ArrayList()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3")
        val selectionArgsMp3 = arrayOf(mimeType)

        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST,
            MediaStore.Audio.AudioColumns.BITRATE
        )
        val c: Cursor? = context.contentResolver.query(
            uri,
            projection,
            selectionMimeType,
            selectionArgsMp3,
            null
        )
        if (c != null) {
            while (c.moveToNext()) {
                val path: Uri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, c.getLong(0))
                val name: String = c.getString(1)
                val artist: String = c.getString(3)
                val bitrate: Int = c.getInt(4)
                val audioModel =
                    AudioModel(
                        name = name,
                        path = path,
                        artist = artist,
                        bitrate = bitrate
                    )
                tempAudioList.add(audioModel)
            }
            c.close()
        }
        Log.d("Audio Repository", tempAudioList.toString())
        return tempAudioList
    }
}