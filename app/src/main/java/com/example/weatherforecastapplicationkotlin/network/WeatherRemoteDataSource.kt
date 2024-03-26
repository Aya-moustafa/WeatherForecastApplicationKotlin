package com.example.weatherforecastapplicationkotlin.network

import android.util.Log
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import retrofit2.Response

class WeatherRemoteDataSource private constructor() : IWeatherRemoteDataSource {
    private val weatherService : ApiServices by lazy {
        OpenWeatherMapRetrofit.retrofitInstance.create(ApiServices::class.java)
    }


    override suspend fun getWeatherForecastOverNetwork(
        lat: Double,
        lon: Double,
        apiKey: String,
        units : String,
        lan:String
    ): Response<WeatherForeCast> {
        val response = weatherService.getWeatherForecast(lat, lon, apiKey,units,lan)
        if (response.isSuccessful) {
            Log.i("TAG", "getAllweathersForeCast remoteDataSource: The WeathersList = ${response.toString()}")
        } else {
            Log.i("TAG", response.errorBody().toString())
        }
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