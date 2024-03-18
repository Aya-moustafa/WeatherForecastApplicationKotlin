package com.example.weatherforecastapplicationkotlin.network

import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.model.WeatherResponse
import com.example.weatherforecastapplicationkotlin.model.WeatherResponseForecast
import retrofit2.Response

interface IWeatherRemoteDataSource {
    suspend fun getWeatherOverNetwork(lat: Double,lon: Double ,apiKey: String , units:String): Response<WeatherResponse>
    suspend fun getWeatherForecastOverNetwork(lat: Double,lon: Double ,apiKey: String, units:String): Response<WeatherForeCast>

}