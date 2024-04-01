package com.example.weatherforecastapplicationkotlin.model.repository

import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun getWeatherForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lan: String
    ): WeatherForeCast?

    suspend fun insertNewPlaceToFavorites(country: Country)
    suspend fun deletePlaceFromFavorites(country: Country)
    suspend fun viewAllFavorites(): Flow<List<Country>>
    suspend fun viewAllHomeWeather(): Flow<WeatherForeCast>
    suspend fun insertTodayWeatherDetails(weatherForeCast: WeatherForeCast)
    suspend fun deleteTodayWeatherDeatils(weatherForeCast: WeatherForeCast)
    suspend fun ClearAllTodayWeatherDeatils()
    suspend fun getRowCount(): Int
    suspend fun getAllNotificationsDate(): Flow<List<NotificationData>>

    suspend fun insertDate(notification: NotificationData)
    suspend fun deleteDate(notification: NotificationData)
    suspend fun deleteNotifiByDate (datetime: String, hourTime: String)
}