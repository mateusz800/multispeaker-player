package com.example.musicplayer.ui.fragment.speakers

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.example.musicplayer.data.model.DeviceModel
import com.example.musicplayer.ui.fragment.browseMusic.MusicIntent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@Composable
fun SpeakersView(viewModel: SpeakerViewModel) {
    val connectedDevices = viewModel.connectedDevices.observeAsState()
    val availableDevices = viewModel.availableDevices.observeAsState()
    SpeakersView(connectedDevices.value, availableDevices.value, viewModel.intent)
}

@Composable
private fun SpeakersView(
    connectedDevices: List<DeviceModel>?,
    availableDevices: List<DeviceModel>?,
    intent: Channel<SpeakersIntent>
) {
    val coroutineScope = rememberCoroutineScope()
    Column {
        Text("Connected devices")
        connectedDevices?.forEach{
            Text(it.name)
        }
        Text("Found devices")
        availableDevices?.forEach {
            Button(onClick = {
                coroutineScope.launch {
                    intent.send(SpeakersIntent.Connect(it.host))
                }
            }) {
                Text(it.name)
            }
        }
    }
}

@Preview
@Composable
private fun SpeakersView_Preview() {
    val availableDevices = listOf(DeviceModel("Sony XA", "192.168.0.168"))
    SpeakersView(null, availableDevices, Channel { })
}