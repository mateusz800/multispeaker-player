package com.example.musicplayer.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.ui.main.components.MenuView
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.example.musicplayer.ui.main.viewState.ScreenState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold {
                        Column {
                            MenuView(viewModel)
                            lifecycleScope.launch {
                                viewModel.currentScreenState.collect{
                                    when(it){
                                        is ScreenState.Music -> { Log.d("Menu","Music")}
                                        is ScreenState.Speakers -> { Log.d("Menu","Speakers")}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MusicPlayerTheme {
        Greeting("Android")
    }
}