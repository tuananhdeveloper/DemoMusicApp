package com.tuananh.app.musicapp.ui.presenter

import com.tuananh.app.musicapp.data.local.SongRepository
import com.tuananh.app.musicapp.data.model.Song
import com.tuananh.app.musicapp.ui.activity.MainView

class MainPresenterImpl(val mainView: MainView): MainPresenter {

    init {
        val songRepository = SongRepository()
        mainView.setSongs(songRepository.fetchAllSongs(mainView.getMainActivityContext()))
    }

    override fun addToFavorites(s: Song) {
        TODO("Not yet implemented")
    }
}
