package com.practicum.playlistmaker.api

import com.practicum.playlistmaker.Track

class ITunesResponse(val resultCount: Int, val results: List<Track>) {
}