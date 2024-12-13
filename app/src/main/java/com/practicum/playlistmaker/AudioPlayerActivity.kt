package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.audioPlayer.AudioPlayerController
import com.practicum.playlistmaker.utils.TRACK
import com.practicum.playlistmaker.utils.Constants

class AudioPlayerActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var player:AudioPlayerController

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val track = intent.getParcelableExtra(TRACK,Track::class.java)

        val btBack = findViewById<Toolbar>(R.id.tb_back)
        val imArtworkUrl512 = findViewById<ImageView>(R.id.im_artwork)
        val txTrackName = findViewById<TextView>(R.id.tx_track_name)
        val txArtistName = findViewById<TextView>(R.id.tx_artist_name)
        val txTrackTimeMillis = findViewById<TextView>(R.id.tx_duration_time)
        val txCollectionName = findViewById<TextView>(R.id.tx_album_name)
        val txReleaseDate = findViewById<TextView>(R.id.tx_year_num)
        val txPrimaryGenreName = findViewById<TextView>(R.id.tx_music_genre)
        val txCountry = findViewById<TextView>(R.id.tx_country_name)
        val imPlay = findViewById<ImageButton>(R.id.playButton)
        val txTrackTimer = findViewById<TextView>(R.id.tx_track_time_play)

        player = AudioPlayerController(track,handler,txTrackTimer,imPlay )

        player.preparePlayer()


        Glide.with(this)
            .load(track?.getCoverArtwork())
            .placeholder(R.drawable.placeholder_player)
            .centerCrop()
            .transform(RoundedCorners(Constants.dpToPx(8f, this)))
            .into(imArtworkUrl512)

        txTrackName.text = track?.trackName
        txArtistName.text = track?.artistName
        txTrackTimeMillis.text = track?.getDuration()
        txCollectionName.text = track?.collectionName
        txReleaseDate.text = track?.getYear()
        txPrimaryGenreName.text = track?.primaryGenreName
        txCountry.text = track?.country


        btBack.setOnClickListener {
            finish()
        }

        imPlay.setOnClickListener{
            player.playbackControl()

        }


    }

    override fun onPause() {
        super.onPause()
        player.pausePlayer()
    }

    override fun onResume() {
        super.onResume()
        player.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

}