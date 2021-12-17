package com.example.musicplayer.di.module

import android.content.Context
import com.example.musicplayer.data.repository.AudioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Singleton
    @Provides
    fun provideAudioRepository(@ApplicationContext context: Context) = AudioRepository(context)

}