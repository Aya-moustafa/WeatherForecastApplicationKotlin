package com.example.weatherforecastapplicationkotlin.network

import com.example.weatherforecastapplicationkotlin.model.Weather
import com.example.weatherforecastapplicationkotlin.model.WeatherResponse

interface IWeatherRemoteDataSource {
    suspend fun getWeatherOverNetwork(lat: Double,lon: Double , exclude: String ,apiKey: String): WeatherResponse
}