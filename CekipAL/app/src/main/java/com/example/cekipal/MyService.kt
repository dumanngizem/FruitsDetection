package com.example.cekipal

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MyService : Service() {
    private lateinit var mediaPlayer : MediaPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.music)
        mediaPlayer.isLooping = true
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }
}

