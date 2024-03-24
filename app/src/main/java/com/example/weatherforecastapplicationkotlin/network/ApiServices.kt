package com.example.weatherforecastapplicationkotlin.network

import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.model.WeatherResponse
import com.example.weatherforecastapplicationkotlin.model.WeatherResponseForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiServices {
    @GET("weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units : String,
        @Query("lang") lan : String
    ): Response<WeatherResponse>


    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units : String,
        @Query("lang") lan : String
    ): Response<WeatherForeCast>
}

