package com.example.weatherforecastapplicationkotlin.network

import com.example.weatherforecastapplicationkotlin.model.Weather
import com.example.weatherforecastapplicationkotlin.model.WeatherResponse

class WeatherRemoteDataSource private constructor() : IWeatherRemoteDataSource {
    private val weatherService : ApiServices by lazy {
        OpenWeatherMapRetrofit.retrofitInstance.create(ApiServices::class.java)
    }

    override suspend fun getWeatherOverNetwork(lat: Double,lon: Double , exclude: String ,apiKey: String) : WeatherResponse{

        val response = weatherService.getWeatherData(lat,lon,exclude,apiKey)
        return response
    }

    companion object{
        private var instance : WeatherRemoteDataSource?=null

        @Synchronized
        fun getInstance() : WeatherRemoteDataSource{
            return instance ?: synchronized(this){
                val temp = WeatherRemoteDataSource()
                instance=temp
                temp
            }
        }
    }
}