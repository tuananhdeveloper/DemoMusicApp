package com.tuananh.app.musicapp.data.local

import com.tuananh.app.musicapp.data.local.source.SongDataSource
import com.tuananh.app.musicapp.data.model.Song

class SongRepository(private val source: SongDataSource): SongDataSource {
    override fun getAllSongs(): MutableList<Song> {
        return source.getAllSongs()
    }
}
