package com.example.musicplayer.ui.main.viewComponent

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicplayer.data.model.AudioModel
import com.example.musicplayer.service.PlayerService
import com.example.musicplayer.ui.fragment.browseMusic.MusicIntent
import com.example.musicplayer.ui.main.MainIntent
import com.example.musicplayer.ui.main.MainViewModel
import com.example.musicplayer.ui.theme.Melon
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@Composable
fun PlayerView(viewModel: MainViewModel) {
    val currentTrack = viewModel.currentTrack.observeAsState()
    val isPause = viewModel.isPause.observeAsState(false)
    if (currentTrack.value != null) {
        PlayerView(
            currentTrack = currentTrack.value!!,
            intent = viewModel.mainIntent,
            isPause = isPause.value
        )
    }
}

@Composable
private fun PlayerView(currentTrack: AudioModel, intent: Channel<MainIntent>, isPause: Boolean) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colors.primary)
                .padding(10.dp)
                .wrapContentSize(align = Alignment.TopStart)
        ) {
            Cover()
            Column(Modifier.padding(start = 10.dp)) {
                Text(
                    currentTrack.artist,
                    color = MaterialTheme.colors.onPrimary, fontSize = 10.sp
                )
                Text(
                    currentTrack.name,
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .align(Alignment.TopEnd)
        ) {
            Button(onClick = {
                coroutineScope.launch {
                    intent.send(MainIntent.Pause)
                }
            }, modifier = Modifier.testTag("pauseButton")) {
                if (isPause) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = "Play button",
                        tint = MaterialTheme.colors.onPrimary,
                    )

                } else {
                    Icon(
                        Icons.Rounded.Pause,
                        contentDescription = "Pause button",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun Cover() {
    Box(
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .background(Melon)
            .zIndex(2f)
        //.shadow(elevation = 2.dp)
    )
}


@Preview
@Composable
private fun Player_Preview() {
    MusicPlayerTheme {
        PlayerView(AudioModel(Uri.EMPTY, "Song title", "Artist"), Channel { }, isPause = true)
    }
}