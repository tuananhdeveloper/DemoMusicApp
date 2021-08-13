package com.tuananh.app.musicapp.ui.activity

import com.tuananh.app.musicapp.data.local.SongRepository
import com.tuananh.app.musicapp.data.model.Song

class SongPresenter(
    private val view: SongContract.View,
    private val songRepository: SongRepository
): SongContract.Presenter {
    override fun getAllSong() {
        view.setSongs(songRepository.getAllSongs())
    }

    override fun addToFavorites(s: Song) {

    }

    override fun start() {
        getAllSong()
    }
}
