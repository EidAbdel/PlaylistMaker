package com.practicum.playlistmaker

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val trackId: Int, // Гуид трека
    val collectionName: String, // Название альбома
    val releaseDate: Date, // Год релиза трека
    val primaryGenreName: String, // Жанр трека
    val country: String // Страна исполнителя
) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    fun getDuration(): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis.toLong())

    fun getYear(): String = SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate)
}

