package com.example.musicplayer.di.module

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import com.example.musicplayer.data.repository.AudioRepository
import com.example.musicplayer.data.repository.DeviceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Singleton
    @Provides
    fun provideAudioRepository(@ApplicationContext context: Context) = AudioRepository(context)

    @Singleton
    @Provides
    fun provideP2pManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager

    @Singleton
    @Provides
    fun provideP2pChannel(@ApplicationContext context: Context): WifiP2pManager.Channel =
        provideP2pManager(context).initialize(context, Looper.getMainLooper(), null)

    @Singleton
    @Provides
    fun provideDeviceRepository(
        @ApplicationContext context: Context,
        manager: WifiP2pManager,
        channel: WifiP2pManager.Channel
    ): DeviceRepository {
        return DeviceRepository(context, manager, channel)
    }


}