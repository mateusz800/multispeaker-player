package com.example.musicplayer.data.repository

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.musicplayer.data.model.AudioModel
import com.example.musicplayer.data.model.DeviceModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceRepository(
    private val context: Context,
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel
) {

    private val _connectedDevices = MutableStateFlow<List<DeviceModel>?>(listOf())
    val connectedDevices: StateFlow<List<DeviceModel>?>
        get() = _connectedDevices

    private val _availableDevices = MutableStateFlow<List<DeviceModel>?>(listOf())
    val availableDevices: StateFlow<List<DeviceModel>?>
        get() = _availableDevices


    fun fetchAvailableDevices() {
        //TODO: permission check
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            print("No permssion")
            return
        }
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                print("Success")
                loadPeersList()
            }

            override fun onFailure(reasonCode: Int) {
                print("failure - reason code - $reasonCode")
            }
        })
    }

    private fun loadPeersList() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            print("no permission")
            return
        }
        manager.requestPeers(channel) { peers: WifiP2pDeviceList? ->
            run {
                if (peers == null) {
                    return@run
                }
                val list: Collection<WifiP2pDevice> = peers.deviceList
                if (list.isNotEmpty()) {
                    val deviceList = mutableListOf<DeviceModel>()
                    list.forEach { wifiP2pDevice ->
                        if (connectedDevices.value?.find { it.host == wifiP2pDevice.deviceAddress } == null) {
                            deviceList.add(
                                DeviceModel(
                                    wifiP2pDevice.deviceName,
                                    wifiP2pDevice.deviceAddress
                                )
                            )
                        }
                    }
                    MainScope().launch {
                        _availableDevices.emit(deviceList)
                    }
                }
            }
        }
    }

    fun addConnectedDevice(deviceAddress: String) {
        MainScope().launch {
            val connectedDevice = availableDevices.value?.find { it.host == deviceAddress }
            if (connectedDevice != null) {
                var newConnectedList = connectedDevices.value?.toMutableList()
                if (newConnectedList == null) newConnectedList = mutableListOf()
                newConnectedList.add(connectedDevice)
                _connectedDevices.emit(newConnectedList)
                val newAvailableList = availableDevices.value?.toMutableList()
                newAvailableList?.remove(connectedDevice)
                _availableDevices.emit(newAvailableList)
            }
        }
    }
}