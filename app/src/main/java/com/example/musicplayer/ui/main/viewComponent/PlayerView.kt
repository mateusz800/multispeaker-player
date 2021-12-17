package com.example.musicplayer.ui.main.viewComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.musicplayer.ui.main.MainViewModel
import com.example.musicplayer.ui.theme.Melon
import com.example.musicplayer.ui.theme.MusicPlayerTheme

@Composable
fun PlayerView(viewModel: MainViewModel) {
    val currentTrack = viewModel.currentTrack.observeAsState()
    if (currentTrack.value != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 0.dp, top = 10.dp, bottom = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Cover()
                Column(
                    modifier = Modifier
                        .offset(x = (-10).dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colors.primary)
                        .padding(20.dp)
                ) {
                    Text(currentTrack.value!!.path, color = MaterialTheme.colors.onPrimary, fontSize = 10.sp)
                    Text(
                        currentTrack.value!!.name,
                        color = MaterialTheme.colors.onPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
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
            .width(100.dp)
            .height(100.dp)
            .background(Melon)
            .zIndex(2f)
        //.shadow(elevation = 2.dp)
    )
}

@Preview
@Composable
private fun Player_Preview() {
    MusicPlayerTheme {
        //PlayerView()
    }
}