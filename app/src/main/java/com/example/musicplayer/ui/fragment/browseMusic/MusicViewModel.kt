package com.example.musicplayer.ui.fragment.browseMusic

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import com.example.musicplayer.data.model.AudioModel
import com.example.musicplayer.data.repository.AudioRepository
import com.example.musicplayer.data.repository.PlayerStateRepository
import com.example.musicplayer.ui.domain.BaseViewModel
import com.example.musicplayer.service.PlayerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    application: Application,
    private val audioRepository: AudioRepository,
    private val playerStateRepository: PlayerStateRepository
) : BaseViewModel(application) {

    val intent = Channel<MusicIntent>(Channel.UNLIMITED)

    private val _allTracks = MutableLiveData<List<AudioModel>>()
    val allTracks: LiveData<List<AudioModel>>
        get() = _allTracks


    init {
        // TODO: initial intent value (code repetition
        _allTracks.postValue(audioRepository.getAllAudioFromDevice())
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is MusicIntent.FetchAudio -> _allTracks.postValue(audioRepository.getAllAudioFromDevice())
                    is MusicIntent.Play -> playTrack(it.track)
                }
            }
        }
    }

    private fun playTrack(track: AudioModel){
        val intent = Intent(PlayerService.Action.CHANGE_TRACK.name)
        intent.putExtra(PlayerService.BroadcastParam.PATH.name, track.path)
        playerStateRepository.updateCurrentTrack(track)
        context.sendBroadcast(intent)
    }
}