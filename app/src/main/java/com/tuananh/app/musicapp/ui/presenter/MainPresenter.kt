package com.tuananh.app.musicapp.ui.presenter

import com.tuananh.app.musicapp.data.model.Song

interface MainPresenter {
    fun addToFavorites(s: Song)
}
