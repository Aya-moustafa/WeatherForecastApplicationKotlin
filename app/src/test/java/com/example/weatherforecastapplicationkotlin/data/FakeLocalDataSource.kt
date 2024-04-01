package com.example.weatherforecastapplicationkotlin.data

import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.ILocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.IWeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_for_home_page.ITodayWeatherLocalDataBase
import com.example.weatherforecastapplicationkotlin.database.data_of_notification.INotificationDeatilsLocalDataSource
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource : ILocalDataSource {

    private val fav_places = mutableListOf<Country>()
    private val alerts_Dtae= mutableListOf<NotificationData>()



    override fun getAllNotifiData(): Flow<List<NotificationData>> {
        return flowOf(alerts_Dtae)
    }

    override suspend fun insertDate(notification: NotificationData) {
           alerts_Dtae.add(notification)
    }

    override suspend fun deleteDate(notification: NotificationData) {
        alerts_Dtae.remove(notification)
    }

    override suspend fun deleteSpcDate(datetime: String, hourTime: String) {
        TODO("Not yet implemented")
    }

    override fun getFavPlaces(): Flow<List<Country>> {
        return flowOf(fav_places)
    }

    override suspend fun insertPlace(country: Country) {
        fav_places.add(country)
    }

    override suspend fun deletePlace(country: Country) {
        fav_places.remove(country)
    }

    override fun getHomeWeather(): Flow<WeatherForeCast> {
        TODO("Not yet implemented")
    }

    override suspend fun insertTodayWeatherDeatils(weatherForeCast: WeatherForeCast) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTodayWeatherDeatils(weatherForeCast: WeatherForeCast) {
        TODO("Not yet implemented")
    }

    override suspend fun clearAllWeatherData() {
        TODO("Not yet implemented")
    }

    override fun getRowCount(): Int {
        TODO("Not yet implemented")
    }
}