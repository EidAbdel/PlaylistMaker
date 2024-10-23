package com.practicum.playlistmaker.preferences


import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.Track

const val HISTORY_LEN = 10

class HistoryTrackPreferences {


    fun readAll(sharedPreferences: SharedPreferences): MutableList<Track> {
        val tracks = mutableListOf<Track>()

        val allPref: Map<String, *> = sharedPreferences.getAll()
        for (value in allPref.values) {
            tracks.add(Gson().fromJson(value.toString(), Track::class.java))
        }

        return tracks

    }

    fun write(sharedPreferences: SharedPreferences, track: Track) {
        val tracks = readAll(sharedPreferences)
        tracks.remove(track)
        tracks.add(0, track)
        sharedPreferences.edit()
            .clear()
            .apply()
        val m = if(HISTORY_LEN > tracks.size) tracks.size else HISTORY_LEN
        for (i in 0..m-1 ) {
            sharedPreferences.edit()
                .putString(i.toString(), Gson().toJson(tracks[i]))
                .apply()
        }
    }


}