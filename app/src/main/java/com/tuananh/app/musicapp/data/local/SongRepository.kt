package com.tuananh.app.musicapp.data.local

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.tuananh.app.musicapp.data.model.Song

class SongRepository {

    fun fetchAllSongs(context: Context): MutableList<Song> {
        val audioList: MutableList<Song> = mutableListOf()
        val contentResolver: ContentResolver = context.contentResolver
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        val cursor: Cursor? = contentResolver.query(uri, null, selection, null, sortOrder)

        cursor?.let {
            while(cursor.moveToNext()) {
                val uri = ContentUris.withAppendedId(uri, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                audioList.add(Song(uri, title, album, artist))
            }
        }
        return audioList
    }
}
