package com.example.musicplayer.ui.fragment.speakers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import com.example.musicplayer.data.repository.DeviceRepository
import javax.inject.Inject

class P2pBroadcastReceiver constructor(private val deviceRepository: DeviceRepository): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                when (state) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        deviceRepository.fetchAvailableDevices()
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                //todo

            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                print("connection changed")
            }
        }
    }
}