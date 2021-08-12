package com.tuananh.app.musicapp.data.local.source

import com.tuananh.app.musicapp.data.model.Song

interface SongDataSource {
    fun getAllSongs(): MutableList<Song>
}