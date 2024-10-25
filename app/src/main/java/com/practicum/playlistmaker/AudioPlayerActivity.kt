package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson

class AudioPlayerActivity : AppCompatActivity() {
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

        val track = Gson().fromJson(intent.getStringExtra("track"), Track::class.java)

        val btBack = findViewById<Toolbar>(R.id.tb_back)
        val imArtworkUrl512 = findViewById<ImageView>(R.id.im_artwork)
        val txTrackName = findViewById<TextView>(R.id.tx_track_name)
        val txArtistName = findViewById<TextView>(R.id.tx_artist_name)
        val txTrackTimeMillis = findViewById<TextView>(R.id.tx_duration_time)
        val txCollectionName = findViewById<TextView>(R.id.tx_album_name)
        val txReleaseDate = findViewById<TextView>(R.id.tx_year_num)
        val txPrimaryGenreName = findViewById<TextView>(R.id.tx_music_genre)
        val txCountry = findViewById<TextView>(R.id.tx_country_name)

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_player)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(imArtworkUrl512)

        txTrackName.text = track.trackName
        txArtistName.text = track.artistName
        txTrackTimeMillis.text = track.getDuration()
        txCollectionName.text = track.collectionName
        txReleaseDate.text = track.getYear()
        txPrimaryGenreName.text = track.primaryGenreName
        txCountry.text = track.country

        btBack.setOnClickListener {
            finish()
        }

    }
}