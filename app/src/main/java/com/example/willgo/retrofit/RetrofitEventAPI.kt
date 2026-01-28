package com.example.willgo.retrofit

import com.example.willgo.data.Event
import retrofit2.Call
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface RetrofitEventAPI {

    @GET("events")
    @Headers("apikey: ZYonRWaShpC1blTRv9GbE4liCIHgGIWd")
    fun getEvents(): Call<List<Event>>



object Client {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://app.ticketmaster.com/discovery/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: RetrofitEventAPI = retrofit.create(RetrofitEventAPI::class.java)


}
}
