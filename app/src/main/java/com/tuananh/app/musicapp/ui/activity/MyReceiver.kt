package com.tuananh.app.musicapp.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver(private val onItemClickListener: MusicNotification.OnItemClickListener)
    : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            MusicNotification.ACTION_PREVIOUS_MUSIC -> {
                onItemClickListener.onPreviousButtonClick()
            }
            MusicNotification.ACTION_NEXT_MUSIC -> {
                onItemClickListener.onNextButtonClick()
            }
            MusicNotification.ACTION_PLAY_MUSIC -> {
                onItemClickListener.onPlayButtonClick()
            }
        }
    }
}
