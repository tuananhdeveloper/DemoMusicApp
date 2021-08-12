package com.tuananh.app.musicapp.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.renderscript.RenderScript
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.tuananh.app.musicapp.R
import com.tuananh.app.musicapp.data.model.Song
import com.tuananh.app.musicapp.service.MediaPlayerService

class MusicNotification(private val service: Service) {

    private var mediaSessionCompat: MediaSessionCompat = MediaSessionCompat(service, TAG)
    private lateinit var notificationManager: NotificationManager

    fun createNotification(song: Song) {

        val notificationIntent = Intent(service, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(service, REQUEST_CODE, notificationIntent, 0)
        val previousPendingIntent = PendingIntent.getBroadcast(service, REQUEST_CODE, Intent(ACTION_PREVIOUS_MUSIC), 0)
        val nextPendingIntent = PendingIntent.getBroadcast(service, REQUEST_CODE, Intent(ACTION_NEXT_MUSIC), 0)
        val playPendingIntent = PendingIntent.getBroadcast(service, REQUEST_CODE, Intent(ACTION_PLAY_MUSIC), 0)

        val actionPrevious = NotificationCompat.Action.Builder(R.drawable.ic_skip_previous, PREVIOUS_BUTTON_TITLE, previousPendingIntent).build()
        val actionPlay = NotificationCompat.Action.Builder(R.drawable.ic_play, PLAY_BUTTON_TITLE, playPendingIntent).build()
        val actionNext = NotificationCompat.Action.Builder(R.drawable.ic_skip_next, NEXT_BUTTON_TITLE, nextPendingIntent).build()

        createNotificationChannel()

        val artwork = BitmapFactory.decodeResource(service.resources, R.drawable.ic_music)

        val notification = NotificationCompat.Builder(service, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_play)
            .setLargeIcon(artwork)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSubText(song.title)
            .setContentIntent(contentPendingIntent)
            .addAction(actionPrevious)
            .addAction(actionPlay)
            .addAction(actionNext)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(service,
                    PlaybackStateCompat.ACTION_STOP))
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSessionCompat.sessionToken))
            .setColor(Color.RED)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = CHANNEL_NAME
            val description = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            notificationManager = service.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    interface OnItemClickListener {
        fun onPlayButtonClick()
        fun onPreviousButtonClick()
        fun onNextButtonClick()
    }

    companion object {
        private const val CHANNEL_NAME = "My Music App"
        private const val CHANNEL_DESCRIPTION = "Music App"
        private const val CHANNEL_ID = "MY_CHANNEL"
        private const val REQUEST_CODE = 400
        private const val PREVIOUS_BUTTON_TITLE = "Previous"
        private const val PLAY_BUTTON_TITLE = "Play"
        private const val NEXT_BUTTON_TITLE = "Next"
        private const val TAG = "tag"
        const val ACTION_CANCEL = "com.tuananh.action.CANCEL"
        const val ACTION_PLAY_MUSIC = "com.tuananh.action.PlayMyMusic"
        const val ACTION_PREVIOUS_MUSIC = "com.tuananh.action.PreviousMusic"
        const val ACTION_NEXT_MUSIC = "com.tuananh.action.NextMusic"

    }
}
