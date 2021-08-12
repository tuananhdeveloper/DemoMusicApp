package com.tuananh.app.musicapp.ui.activity

import android.content.Context
import com.tuananh.app.musicapp.base.BasePresenter
import com.tuananh.app.musicapp.data.model.Song

interface SongContract {
    interface View {
        fun setSongs(songs: MutableList<Song>)
        fun getMainActivityContext(): Context
    }

    interface Presenter: BasePresenter {
        fun getAllSong()
        fun addToFavorites(s: Song)
    }
}