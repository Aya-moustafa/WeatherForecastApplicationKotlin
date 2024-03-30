package com.example.weatherforecastapplicationkotlin.notification_feature.view

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.WeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_for_home_page.TodayWeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_of_notification.NotificationDeatilsLocalDataSource
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModelFactory
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource

class DeleteNotitificationWorker (appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams)  {
    override fun doWork(): Result {
        val notificationId = inputData.getInt("notificationId", -1)
        if (notificationId != -1) {
            val weatherViewModel = WeatherViewModelFactory(
                WeatherRepository.getInstance(
                    WeatherRemoteDataSource.getInstance(),
                    WeatherLocalDataSource(applicationContext),
                    TodayWeatherLocalDataSource(applicationContext),
                    NotificationDeatilsLocalDataSource(applicationContext)
                ), applicationContext
            ).create(WeatherViewModel::class.java)
            weatherViewModel.setNotificationIdToDelete(notificationId)
        }
        return Result.success()
    }
}