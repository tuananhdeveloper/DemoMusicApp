package com.tuananh.app.musicapp.data.model

import android.net.Uri

data class Song(
    val uri: Uri,
    val title: String,
    val album: String,
    val artist: String
)
