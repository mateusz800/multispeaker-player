package com.example.musicplayer.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.ui.main.viewComponent.MenuView
import com.example.musicplayer.ui.fragment.browseMusic.MusicView
import com.example.musicplayer.ui.fragment.speakers.SpeakersView
import com.example.musicplayer.ui.main.viewComponent.PlayerView
import com.example.musicplayer.ui.theme.MusicPlayerTheme


enum class Destinations {
    MUSIC,
    SPEAKERS;

    val value: String
        get() = this.name
}

@Composable
fun MainView(viewModel: MainViewModel) {
    val navController = rememberNavController()

    MusicPlayerTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(bottomBar = { PlayerView(hiltViewModel()) }) {
                Column {
                    MenuView(viewModel, navController)
                    NavHost(
                        navController = navController,
                        startDestination = Destinations.MUSIC.value
                    ) {
                        composable(Destinations.MUSIC.value) {
                            MusicView(hiltViewModel())
                        }
                        composable(Destinations.SPEAKERS.value) {
                            SpeakersView(hiltViewModel())
                        }
                        /*...*/
                    }
                }
            }
        }
    }
}
