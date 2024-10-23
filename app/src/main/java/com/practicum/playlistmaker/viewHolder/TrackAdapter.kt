package com.practicum.playlistmaker.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.HISTORY_PREFERENCES
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Track
import com.practicum.playlistmaker.preferences.HistoryTrackPreferences


class TrackAdapter(
    private val tracks: List<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_holder_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {

        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { view ->

            HistoryTrackPreferences().write(
                view.context.getSharedPreferences(
                    HISTORY_PREFERENCES,
                    MODE_PRIVATE
                ), this.tracks[position]
            )

        }
    }
}