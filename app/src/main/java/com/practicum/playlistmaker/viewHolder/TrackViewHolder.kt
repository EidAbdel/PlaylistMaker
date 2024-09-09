package com.practicum.playlistmaker.viewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Track

class TrackViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    private val trackName: TextView // Название композиции
    private val artistName: TextView // Имя исполнителя
    private val trackTime: TextView // Продолжительность трека
    private val artworkUrl100: ImageView // Ссылка на изображение обложки

    init {
        trackName = item.findViewById(R.id.tx_track_name)
        artistName = item.findViewById(R.id.tx_artist_name)
        trackTime = item.findViewById(R.id.tx_track_time)
        artworkUrl100 = item.findViewById(R.id.im_artwork)
    }

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(artworkUrl100)
    }


}