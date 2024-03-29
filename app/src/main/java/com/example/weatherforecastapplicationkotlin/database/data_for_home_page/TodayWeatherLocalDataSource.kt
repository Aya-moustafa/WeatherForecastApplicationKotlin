package com.example.weatherforecastapplicationkotlin.database.data_for_home_page

import android.content.Context
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import kotlinx.coroutines.flow.Flow

class TodayWeatherLocalDataSource(context: Context) : ITodayWeatherLocalDataBase {
    private val dao : ITodayWeatherLocalDataBase by lazy {
        val db : WeatherDataBase = WeatherDataBase.getInstance(context)
        db.getTodayWeatherDao()
    }
    override fun getHomeWeather(): Flow<WeatherForeCast> {
      return  dao.getHomeWeather()
    }

    override suspend fun insertTodayWeatherDeatils(weatherForeCast: WeatherForeCast) {
      dao.insertTodayWeatherDeatils(weatherForeCast)
    }

    override suspend fun deleteTodayWeatherDeatils(weatherForeCast: WeatherForeCast) {
      dao.deleteTodayWeatherDeatils(weatherForeCast)
    }

    override suspend fun clearAllWeatherData() {
        dao.clearAllWeatherData()
    }

    override fun getRowCount(): Int {
       return dao.getRowCount()
    }
}