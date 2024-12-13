package com.practicum.playlistmaker.audioPlayer

import android.media.MediaPlayer
import android.os.Handler
import android.widget.ImageButton
import android.widget.TextView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerController(
    private val track: Track?,
    private val handler: Handler,
    private val txtDuration: TextView,
    private val imPlay: ImageButton
) {
    private var playerState = STATE_DEFAULT
    private var url = track?.previewUrl
    private var mediaPlayer = MediaPlayer()
    private var runnable = Runnable { getTrackTimeLeft() }
    fun release() {
        mediaPlayer.release()
    }

    fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            imPlay.isEnabled = true
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            imPlay.setBackgroundResource(R.drawable.button_play)
            txtDuration.text = "00:00"
        }
    }

    fun playbackControl() {
        when(this.playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                stopTimer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                startTimer()
            }
        }
    }

    fun startPlayer() {
        mediaPlayer.start()
        imPlay.setBackgroundResource(R.drawable.button_pause)
        playerState = STATE_PLAYING
    }

    fun pausePlayer() {
        when (this.playerState) {
            STATE_PLAYING -> {
                mediaPlayer.pause()
                imPlay.setBackgroundResource(R.drawable.button_play)
                playerState = STATE_PAUSED
            }
        }
    }

    private fun getTrackTimeLeft(){
        txtDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    private fun refreshTrackTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING){
                    getTrackTimeLeft()
                    handler.postDelayed(this,DELAY_MILLS)
                }
            }
        }
    }
    private fun startTimer() {
        runnable = refreshTrackTimer()
        handler.post(runnable)
    }

    private fun stopTimer() {
        runnable.let { handler.removeCallbacks(it) }
    }




    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY_MILLS = 300L

    }
}