package com.tuananh.app.musicapp.data.local.source

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.tuananh.app.musicapp.data.model.Song

class SongDataSourceImpl(private val contentResolver: ContentResolver): SongDataSource {

    override fun getAllSongs(): MutableList<Song> {
        val audioList: MutableList<Song> = mutableListOf()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        val cursor: Cursor? = contentResolver.query(uri, null, selection, null, sortOrder)

        cursor?.let {
            while(it.moveToNext()) {
                val uri = ContentUris.withAppendedId(uri, it.getLong(it.getColumnIndex(
                    MediaStore.Audio.Media._ID)))
                val title = it.getString(it.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val album = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artist = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                audioList.add(Song(uri, title, album, artist))
            }
        }
        return audioList
    }

}