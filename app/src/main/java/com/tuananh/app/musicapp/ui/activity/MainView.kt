package com.tuananh.app.musicapp.ui.activity

import android.content.Context
import com.tuananh.app.musicapp.data.model.Song

interface MainView {
    fun setSongs(songs: MutableList<Song>)
    fun getMainActivityContext(): Context
}
