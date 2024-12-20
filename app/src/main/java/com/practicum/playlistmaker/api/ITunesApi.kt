package com.practicum.playlistmaker.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ITunesApi {
    @GET("/search?entity=song")
    fun findSong(@Query("term") text:String): Call<ITunesResponse>
}