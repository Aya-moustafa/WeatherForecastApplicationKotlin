package com.example.weatherforecastapplicationkotlin.data

import com.example.weatherforecastapplicationkotlin.model.City
import com.example.weatherforecastapplicationkotlin.model.Coord
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.model.WeatherItem
import com.example.weatherforecastapplicationkotlin.network.IWeatherRemoteDataSource
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.lang.Exception

class FakeRemoteDataSource() : IWeatherRemoteDataSource {
    var weather = WeatherForeCast(
    "2024/03/28",
        0,
        0,
         mutableListOf<WeatherItem>(),
         City(1,"Nasr City", Coord(30.123,31.29),"Egypt" ,0,0,0,0)

    )

    override suspend fun getWeatherForecastOverNetwork(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lan: String
    ): Response<WeatherForeCast> {
         return Response.success(weather)
    }
}