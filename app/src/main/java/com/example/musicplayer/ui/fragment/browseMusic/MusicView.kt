package com.example.musicplayer.ui.fragment.browseMusic

import android.net.Uri
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.data.model.AudioModel
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@Composable
fun MusicView(viewModel: MusicViewModel) {
    val allTracks = viewModel.allTracks.observeAsState(listOf())
    MusicView(allTracks.value, viewModel.intent)
}

@Composable
private fun MusicView(
    tracks: List<AudioModel>,
    intent: Channel<MusicIntent>,
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
            .scrollable(scrollState, orientation = Orientation.Vertical)

    ) {
        item {
            Text(
                "Browse",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
        }
        for (track in tracks) {
            item {
                Button(onClick = {
                    coroutineScope.launch {
                        intent.send(MusicIntent.Play(track))
                    }
                }) {
                    Text(track.name)
                }
            }
        }
    }
}


@Composable
@Preview
fun MusicView_Preview() {
    MusicPlayerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            val tracks = mutableListOf<AudioModel>()
            tracks.apply {
                add(
                    AudioModel(
                        name = "Send Me Love",
                        path = android.net.Uri.EMPTY,
                        artist = "Jane Kramer"
                    )
                )
            }
            MusicView(tracks, Channel())
        }
    }
}