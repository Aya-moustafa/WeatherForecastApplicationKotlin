package com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places

import com.example.weatherforecastapplicationkotlin.model.AllDaos
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val allDaos: AllDaos) : ILocalDataSource {

     override fun getFavPlaces(): Flow<List<Country>> {
        return  allDaos.favDao.getFavPlaces()
    }

     override suspend fun insertPlace(country: Country) {
        allDaos.favDao.insertPlace(country)
    }

     override suspend fun deletePlace(country: Country) {
        allDaos.favDao.deletePlace(country)
    }

     override fun getHomeWeather(): Flow<WeatherForeCast> {
        return  allDaos.home_details_dao.getHomeWeather()
    }

     override suspend fun insertTodayWeatherDeatils(weatherForeCast: WeatherForeCast) {
         allDaos.home_details_dao.insertTodayWeatherDeatils(weatherForeCast)
    }

     override suspend fun deleteTodayWeatherDeatils(weatherForeCast: WeatherForeCast) {
         allDaos.home_details_dao.deleteTodayWeatherDeatils(weatherForeCast)
    }

     override suspend fun clearAllWeatherData() {
         allDaos.home_details_dao.clearAllWeatherData()
    }

     override fun getRowCount(): Int {
        return allDaos.home_details_dao.getRowCount()
    }

    override fun getAllNotifiData(): Flow<List<NotificationData>> {
        return  allDaos.alertDao.getAllNotifiData()
    }

    override suspend fun insertDate(notification: NotificationData) {
        allDaos.alertDao.insertDate(notification)
    }

    override suspend fun deleteDate(notification: NotificationData) {
        allDaos.alertDao.deleteDate(notification)
    }

    override suspend fun deleteSpcDate(datetime: String, hourTime: String) {
        allDaos.alertDao.deleteNotifiByDate(datetime , hourTime)
    }
}