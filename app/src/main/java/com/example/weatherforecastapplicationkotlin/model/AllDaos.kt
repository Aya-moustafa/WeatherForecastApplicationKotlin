package com.example.weatherforecastapplicationkotlin.model

import android.content.Context
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.HomeDataBase
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.IWeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_for_home_page.ITodayWeatherLocalDataBase
import com.example.weatherforecastapplicationkotlin.database.data_for_home_page.WeatherDataBase
import com.example.weatherforecastapplicationkotlin.database.data_of_notification.INotificationDeatilsLocalDataSource

class AllDaos (context : Context) {
      val favDao : IWeatherLocalDataSource = HomeDataBase.getInstance(context).getFavoritesDao()
      val alertDao : INotificationDeatilsLocalDataSource = HomeDataBase.getInstance(context).getNotDateDao()
      val home_details_dao : ITodayWeatherLocalDataBase = WeatherDataBase.getInstance(context).getTodayWeatherDao()
}