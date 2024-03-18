package com.example.weatherforecastapplicationkotlin.model

import android.util.Log
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import kotlin.math.log

class WeatherRepository(val remoteDataSource: WeatherRemoteDataSource) {

    private lateinit var weatherForecastList : WeatherResponseForecast

    companion object {
        @Volatile
        private var instance: WeatherRepository? = null
        fun getInstance(
            remoteDataSource: WeatherRemoteDataSource
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepository(remoteDataSource).also { instance = it }
            }
        }
    }


     suspend fun getWeatherDetails (lat: Double,lon: Double ,apiKey: String ,units : String) : WeatherResponse? {
         val response = remoteDataSource.getWeatherOverNetwork(lat, lon, apiKey,units)
         return if (response.isSuccessful) {
             // Return the response body
             Log.i("TAG", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<getWeatherDetails: ${response.body()}>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
             response.body()
         } else {
             Log.i("TAG", response.errorBody()?.string() ?: "Unknown error")
             null // Return null in case of error
         }
     }

     suspend fun getWeatherForecast (lat: Double,lon: Double ,apiKey: String,units : String) : WeatherForeCast? {
         val response = remoteDataSource.getWeatherForecastOverNetwork(lat,lon,apiKey,units)
         return if(response.isSuccessful){
             Log.i("TAG", "getWeatherForeCast Repository: The responseofWeather = ${response.body()}")
             response.body()

         }else {
             Log.i("TAG", response.errorBody().toString())
             null
         }

     }

}