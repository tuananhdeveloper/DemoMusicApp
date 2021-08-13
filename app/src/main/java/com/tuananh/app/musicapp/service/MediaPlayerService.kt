package com.tuananh.app.musicapp.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.tuananh.app.musicapp.data.model.Song
import com.tuananh.app.musicapp.ui.activity.MusicNotification


class MediaPlayerService: Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var onMediaPreparedListener: (mp: MediaPlayer?) -> Unit
    private lateinit var song: Song
    private val iBinder: IBinder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return iBinder
    }

    override fun onCompletion(mp: MediaPlayer?) {

    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
        onMediaPreparedListener(mp)

        val notification = MusicNotification(this)
        notification.createNotification(song)
    }

    fun playMedia() {
        mediaPlayer.start()
    }

    fun currentSong() = song

    fun isMediaPlaying() = mediaPlayer.isPlaying

    fun setOnMediaPreparedListener(onMediaPreparedListener: (mp: MediaPlayer?) -> Unit) {
        this.onMediaPreparedListener = onMediaPreparedListener
    }

    fun initMediaPlayer(song: Song) {
        this.song = song

        if(::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }

        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener(this@MediaPlayerService)
            setOnPreparedListener(this@MediaPlayerService)
            reset()
            setAudioAttributes(AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()
            )
            setDataSource(baseContext, song.uri)
            prepareAsync();
        }
    }

    fun pauseMedia() {
        mediaPlayer.pause()
    }

    fun stopMedia() {
        if(mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    fun getMediaDuration(): Int {
        return mediaPlayer.duration
    }

    fun getMediaCurrentPostion(): Int {
        return mediaPlayer.currentPosition
    }

    fun seekTo(msec: Int) {
        return mediaPlayer.seekTo(msec)
    }

    inner class LocalBinder: Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }
}
