package com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places

import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    fun getFavPlaces(): Flow<List<Country>>

    suspend fun insertPlace(country: Country)

    suspend fun deletePlace(country: Country)
    fun getHomeWeather(): Flow<WeatherForeCast>

    suspend fun insertTodayWeatherDeatils(weatherForeCast: WeatherForeCast)

    suspend fun deleteTodayWeatherDeatils(weatherForeCast: WeatherForeCast)

    suspend fun clearAllWeatherData()
    fun getRowCount(): Int
    fun getAllNotifiData(): Flow<List<NotificationData>>

    suspend fun insertDate(notification: NotificationData)

    suspend fun deleteDate(notification: NotificationData)

    suspend fun deleteSpcDate (datetime: String , hourTime : String)
}