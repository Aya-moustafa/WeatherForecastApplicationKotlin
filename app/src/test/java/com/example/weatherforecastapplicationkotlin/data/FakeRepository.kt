package com.example.weatherforecastapplicationkotlin.data

import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.model.repository.IWeatherRepository
import com.example.weatherforecastapplicationkotlin.model.repository.WeatherRepository
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeRepository : IWeatherRepository {
    private val fakePlaces = mutableListOf<Country>()
    private val fakeWeatherForecast = MutableStateFlow<WeatherForeCast?>(null)
    private val fakeNotifications = mutableListOf<NotificationData>()


    fun setFakePlaces(places: List<Country>) {
        fakePlaces.clear()
        fakePlaces.addAll(places)
    }

    fun setFakeNotification(notification: List<NotificationData>) {
        fakeNotifications.clear()
        fakeNotifications.addAll(notification)
    }

    override suspend fun getWeatherForecast(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lan: String
    ): WeatherForeCast? {
        TODO("Not yet implemented")
    }

    override suspend fun insertNewPlaceToFavorites(country: Country) {
        fakePlaces.add(country)
    }

    override suspend fun deletePlaceFromFavorites(country: Country) {
        fakePlaces.remove(country)
    }

    override suspend fun viewAllFavorites(): Flow<List<Country>> {
        return flowOf(fakePlaces)
    }

    override suspend fun viewAllHomeWeather(): Flow<WeatherForeCast> {
        TODO("Not yet implemented")
    }

    override suspend fun insertTodayWeatherDetails(weatherForeCast: WeatherForeCast) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTodayWeatherDeatils(weatherForeCast: WeatherForeCast) {
        TODO("Not yet implemented")
    }

    override suspend fun ClearAllTodayWeatherDeatils() {
        TODO("Not yet implemented")
    }

    override suspend fun getRowCount(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getAllNotificationsDate(): Flow<List<NotificationData>> {
        return  flowOf(fakeNotifications)
    }

    override suspend fun insertDate(notification: NotificationData) {
        fakeNotifications.add(notification)
    }

    override suspend fun deleteDate(notification: NotificationData) {
        fakeNotifications.remove(notification)
    }
}