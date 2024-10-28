package com.practicum.playlistmaker

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



data class Track(
    val trackName: String?, // Название композиции
    val artistName: String?, // Имя исполнителя
    val trackTimeMillis: String?, // Продолжительность трека
    val artworkUrl100: String?, // Ссылка на изображение обложки
    val trackId: Int, // Гуид трека
    val collectionName: String?, // Название альбома
    val releaseDate: Date?, // Год релиза трека
    val primaryGenreName: String?, // Жанр трека
    val country: String? // Страна исполнителя
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        SimpleDateFormat("dd-mm-yyyy").parse(parcel.readString()),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
    fun getDuration(): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis!!.toLong())

    fun getYear(): String = SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate)
    fun getDate(): String = SimpleDateFormat("dd-mm-yyyy", Locale.getDefault()).format(releaseDate)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeString(trackTimeMillis)
        parcel.writeString(artworkUrl100)
        parcel.writeInt(trackId)
        parcel.writeString(collectionName)
        parcel.writeString(this.getDate())
        parcel.writeString(primaryGenreName)
        parcel.writeString(country)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}

