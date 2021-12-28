package com.example.musicplayer.ui.fragment.speakers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.DeviceModel
import com.example.musicplayer.data.repository.DeviceRepository
import com.example.musicplayer.ui.domain.BaseViewModel
import com.example.musicplayer.ui.fragment.browseMusic.MusicIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpeakerViewModel @Inject constructor(
    application: Application,
    private val deviceRepository: DeviceRepository
) : BaseViewModel(application) {
    private val _connectedDevices = MutableLiveData<List<DeviceModel>>(listOf())
    val connectedDevices: LiveData<List<DeviceModel>>
        get() = _connectedDevices

    private val _availableDevices = MutableLiveData<List<DeviceModel>>(listOf())
    val availableDevices: LiveData<List<DeviceModel>>
        get() = _availableDevices

    val intent = Channel<SpeakersIntent>(Channel.UNLIMITED)

    private var p2pBroadcastReceiver: BroadcastReceiver? = null

    @Inject
    lateinit var manager: WifiP2pManager

    @Inject
    lateinit var channel: WifiP2pManager.Channel


    init {
        setUpBroadcastReceiver()
        handleIntent()
        collectAvailableDevices()
        collectConnectedDevices()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is SpeakersIntent.Connect -> connectToDevice(it.host)
                }
            }
        }
    }

    private fun collectConnectedDevices() {
        viewModelScope.launch {
            deviceRepository.connectedDevices.collect {
                if (it != null) {
                    _connectedDevices.postValue(it)
                }
            }
        }
    }

    private fun collectAvailableDevices(){
        viewModelScope.launch {
            deviceRepository.availableDevices.collect {
                if (it != null) {
                    _availableDevices.postValue(it)
                }
            }
        }
    }

    private fun setUpBroadcastReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.apply {
            // Indicates a change in the Wi-Fi P2P status.
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            // Indicates a change in the list of available peers.
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            // Indicates the state of Wi-Fi P2P connectivity has changed.
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            // Indicates this device's details have changed.
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        p2pBroadcastReceiver = P2pBroadcastReceiver(deviceRepository)
        context.registerReceiver(p2pBroadcastReceiver, intentFilter)
    }


    private fun connectToDevice(deviceAddress: String) {
        //TODO: permission check
        val config = WifiP2pConfig()
        config.deviceAddress = deviceAddress
        channel.also { channel ->
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                print("no permission")
                return
            }
            manager.connect(channel, config, object : WifiP2pManager.ActionListener {

                override fun onSuccess() {
                    manager.requestConnectionInfo(channel, WifiP2pManager.ConnectionInfoListener { info ->
                        val ipAddress = info.groupOwnerAddress.hostAddress
                        deviceRepository.addConnectedDevice(deviceAddress, ipAddress)
                    })

                }

                override fun onFailure(reason: Int) {
                    //failure logic
                    print("failure")
                }
            })
        }
    }


}