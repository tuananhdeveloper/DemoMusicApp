package com.tuananh.app.musicapp.ui.activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tuananh.app.musicapp.R
import com.tuananh.app.musicapp.base.BaseActivity
import com.tuananh.app.musicapp.data.local.SongRepository
import com.tuananh.app.musicapp.data.local.source.SongDataSourceImpl
import com.tuananh.app.musicapp.data.model.Song
import com.tuananh.app.musicapp.databinding.ActivityMainBinding
import com.tuananh.app.musicapp.service.MediaPlayerService
import com.tuananh.app.musicapp.ui.adapter.SongAdapter
import com.tuananh.app.musicapp.util.Converter

class MainActivity : BaseActivity<ActivityMainBinding>(), SongContract.View, SeekBar.OnSeekBarChangeListener, View.OnClickListener,
MusicNotification.OnItemClickListener {

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding = ActivityMainBinding::inflate
    private lateinit var songAdapter: SongAdapter
    private lateinit var myMPService: MediaPlayerService
    private lateinit var songs: MutableList<Song>
    private var serviceBound = false
    private var seeking = false
    private var connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceBound = true
            val binder = service as MediaPlayerService.LocalBinder
            myMPService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }
    }

    override fun init() {
        registerBroadcastReceiver()
        checkAndRequestPermissions()
    }

    override fun onClick(v: View?) {
        with(binding.includedBottomSheet) {
            when(v) {
               viewControl.btnPlay -> {
                    if(!myMPService.isMediaPlaying()) {
                        changePlayState(true)

                    }
                    else {
                        changePlayState(false)
                    }
               }

                playerBar.btnSmallPlay -> {
                    if(!myMPService.isMediaPlaying()) {
                        changePlayState(true)
                    }
                    else {
                        changePlayState(false)
                    }
                }

                viewControl.btnSkipNext -> {
                    playNextSong()

                }

                viewControl.btnSkipPrevious -> {
                    playPreviousSong()
                }
            }
        }

    }

    override fun onNextButtonClick() = playNextSong()

    override fun onPlayButtonClick() {
        if(!myMPService.isMediaPlaying()) {
            changePlayState(true)
        }
        else {
            changePlayState(false)
        }
    }

    override fun onPreviousButtonClick() = playPreviousSong()

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        seeking = true
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.progress?.let {
            if(::myMPService.isInitialized) {
                myMPService.seekTo(it)
            }
        }
        seeking = false
    }

    override fun setSongs(songs: MutableList<Song>) {
        this.songs = songs
        with(songAdapter) {
            setSongList(songs)
            notifyDataSetChanged()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    performAction()
                } else {

                }
                return
            }
            else -> {}
        }
    }

    override fun getMainActivityContext(): Context {
        return baseContext
    }

    private fun registerBroadcastReceiver() {
        val myReceiver = MyReceiver(this)
        val intentFilter = IntentFilter().apply {
            addAction(MusicNotification.ACTION_NEXT_MUSIC)
            addAction(MusicNotification.ACTION_PREVIOUS_MUSIC)
            addAction(MusicNotification.ACTION_PLAY_MUSIC)
        }
        registerReceiver(myReceiver, intentFilter)
    }

    private fun playNextSong() {
        val currenSong = myMPService.currentSong()
        val nextIndex = songs.indexOf(currenSong)+1
        if(nextIndex < songs.size) {
            myMPService.initMediaPlayer(songs[nextIndex])
            showBottomSheet(songs[nextIndex])
            changePlayState(true)
        }
    }

    private fun playPreviousSong() {
        val currenSong = myMPService.currentSong()
        val previousIndex = songs.indexOf(currenSong)-1
        if(previousIndex >= 0){
            myMPService.initMediaPlayer(songs[previousIndex])
            showBottomSheet(songs[previousIndex])
            changePlayState(true)
        }
    }

    private fun playMusic(song: Song) {
        myMPService.initMediaPlayer(song)
    }

    private fun checkAndRequestPermissions() {
        if(ContextCompat.checkSelfPermission(this, READ_STORAGE_PERMISSION)
            == PackageManager.PERMISSION_GRANTED) {
            performAction()
        }
        else {
            requestPermissions(this, arrayOf(READ_STORAGE_PERMISSION), REQUEST_CODE)
        }
    }

    private fun performAction() {
        initService()
        initBottomSheet()
        initRecyclerView()
        initPresenter()
    }

    private fun initPresenter() {
        SongPresenter(this, SongRepository(SongDataSourceImpl(contentResolver))).start()
    }

    private fun initService() {

        Intent(this, MediaPlayerService::class.java).also { intent ->
            startService(intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun onItemClick(view: View?, position: Int): Unit {
        val song = songs[position]
        myMPService.initMediaPlayer(song)
        showBottomSheet(song)
    }

    private fun initRecyclerView() {
        val myRecyclerView = binding.songList
        songAdapter = SongAdapter(this::onItemClick)
        myRecyclerView.adapter = songAdapter
        myRecyclerView.layoutManager = LinearLayoutManager(this)
        myRecyclerView.setHasFixedSize(true)
    }

    private fun initBottomSheet() {
        val bottomSheet: ConstraintLayout = binding.includedBottomSheet.bottomSheet
        val sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        with(binding.includedBottomSheet) {
            playerBar.btnSmallPlay.setOnClickListener(this@MainActivity)
            viewControl.btnPlay.setOnClickListener(this@MainActivity)
            viewControl.btnSkipNext.setOnClickListener(this@MainActivity)
            viewControl.btnSkipPrevious.setOnClickListener(this@MainActivity)
            viewControl.seekBar.setOnSeekBarChangeListener(this@MainActivity)
            hide(childBottomSheet, true)
        }

        sheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                darkenContentLayoutBackground(slideOffset)

                with(binding.includedBottomSheet) {
                    if(slideOffset == COLLAPSED_STATE) {
                        playerBar.root.alpha = 1 - slideOffset
                        childBottomSheet.alpha = slideOffset

                        hide(childBottomSheet, true)
                        show(playerBar.root)
                    }
                    else if(slideOffset == EXPANDED_STATE) {
                        show(childBottomSheet)
                        hide(playerBar.root, true)
                    }
                    else {
                        playerBar.root.alpha = 0.5f - slideOffset
                        childBottomSheet.alpha = slideOffset - 0.2f

                        show(childBottomSheet)
                        show(playerBar.root)
                    }
                }
            }
        })
    }

    private fun darkenContentLayoutBackground(slideOffset: Float) {
        with(binding.contentLayout) {
            setBackgroundColor(Color.BLACK)
            background.alpha = (slideOffset*100).toInt()
        }
    }

    private fun changePlayState(play: Boolean) {
        with(binding.includedBottomSheet) {
            if(play) {
                viewControl.btnPlay.setBackgroundResource(R.drawable.pause_button)
                playerBar.btnSmallPlay.setImageResource(R.drawable.ic_pause)
                myMPService.playMedia()
            }
            else {
                viewControl.btnPlay.setBackgroundResource(R.drawable.play_button)
                playerBar.btnSmallPlay.setImageResource(R.drawable.ic_play)
                myMPService.pauseMedia()
            }
        }
    }

    private fun show(v: View) {
        v.visibility = View.VISIBLE
    }

    private fun hide(v: View, gone: Boolean) {
        if(gone) {
            v.visibility = View.GONE
        }
        else v.visibility = View.INVISIBLE
    }

    private fun showBottomSheet(song: Song) {
        with(binding.includedBottomSheet) {
            myMPService.setOnMediaPreparedListener {
                show(root)
                updatePlayerBar(song)
                updateBottomSheetToolbarAndTitle(song)
                updateViewControls()

                Thread(Runnable {
                    while(true) {
                        Thread.sleep(DELAY.toLong())
                        runOnUiThread {
                            updateSeekBarAndStartTime()
                        }
                    }
                }).start()
            }
        }
    }

    private fun updateViewControls() {
        with(binding.includedBottomSheet.viewControl) {
            btnPlay.setBackgroundResource(R.drawable.pause_button)
            textEndTime.text = Converter.convertDuration(myMPService.getMediaDuration())
            textStartTime.text = Converter.convertDuration(0)
            seekBar.max = myMPService.getMediaDuration()
        }
    }

    private fun updateBottomSheetToolbarAndTitle(song: Song) {
        with(binding.includedBottomSheet) {
            textAuthor2.text = song.artist
            myAppbar.toolbarTitle.text = song.title
        }
    }

    private fun updatePlayerBar(song: Song) {
        with(binding.includedBottomSheet.playerBar) {
            btnSmallPlay.setImageResource(com.tuananh.app.musicapp.R.drawable.ic_pause)
            textAuthor.text = song.artist
            textSongName.text = song.title
        }
    }

    private fun updateSeekBarAndStartTime() {
        with(binding.includedBottomSheet.viewControl) {
            seekBar.progress = myMPService.getMediaCurrentPostion()
            textStartTime.text = Converter.convertDuration(myMPService.getMediaCurrentPostion())
        }
    }

    companion object {
        private const val COLLAPSED_STATE = 0.0f
        private const val EXPANDED_STATE = 1.0f
        private const val READ_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val REQUEST_CODE = 333
        private const val DELAY = 400
    }
}
