package com.example.musicplayer.ui.main.viewComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.musicplayer.ui.main.MainIntent
import com.example.musicplayer.ui.main.MainViewModel
import com.example.musicplayer.ui.main.viewState.ScreenState
import kotlinx.coroutines.launch

enum class Navigation(
    val title: String,
    val state: ScreenState,
    val intentAction: MainIntent
) {
    MUSIC("Music", ScreenState.Music, MainIntent.NavigateToMusic),
    SPEAKERS("Speakers", ScreenState.Speakers, MainIntent.NavigateToSpeakers),
    SETTINGS("Settings", ScreenState.Settings, MainIntent.NavigateToSettings)
}

@Composable
fun MenuView(viewModel: MainViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState(0)
    val activeScreen = viewModel.currentScreenState.collectAsState()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .scrollable(orientation = Orientation.Horizontal, state = scrollState)
    ) {

        items(1) {
            Spacer(Modifier.width(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Navigation.values().forEach {
                    MenuButton(
                        text = it.title,
                        isActive = activeScreen.value == it.state,
                        clickFunc = {
                            coroutineScope.launch {
                                viewModel.mainIntent.send(it.intentAction)
                            }
                        })
                }
            }
        }
    }
}

@Composable
private fun MenuButton(text: String, isActive: Boolean, clickFunc: () -> Unit) {
    //TODO: add animation when active
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .defaultMinSize(minWidth = 150.dp)
            .height(if (isActive) 50.dp else 40.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(if (isActive) MaterialTheme.colors.primary else MaterialTheme.colors.secondary)
            .padding(horizontal = 5.dp)
            .clickable { clickFunc() }

    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colors.background)
                .width(50.dp)
                .height(if (isActive) 35.dp else 30.dp)
                .shadow(elevation = (-25).dp, clip = false)

        ) {

        }
        Text(text, color = MaterialTheme.colors.onPrimary)
    }
}


// TODO
/*
@Composable
@Preview
private fun MenuView_Preview() {
    MusicPlayerTheme {
        MenuView(hiltViewModel())
    }
}
 */