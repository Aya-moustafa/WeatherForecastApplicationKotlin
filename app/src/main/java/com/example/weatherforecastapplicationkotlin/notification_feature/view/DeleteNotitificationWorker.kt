package com.example.weatherforecastapplicationkotlin.notification_feature.view

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.LocalDataSource
import com.example.weatherforecastapplicationkotlin.model.AllDaos
import com.example.weatherforecastapplicationkotlin.model.repository.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.notification_feature.NotifViewModel.NotificationViewModel
import com.example.weatherforecastapplicationkotlin.notification_feature.NotifViewModel.NotificationViewModelFactory

class DeleteNotitificationWorker (appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams)  {
    override fun doWork(): Result {
        val notificationId = inputData.getInt("notificationId", -1)
        if (notificationId != -1) {
            val weatherViewModel = NotificationViewModelFactory(
                WeatherRepository.getInstance(
                    WeatherRemoteDataSource.getInstance() , LocalDataSource(AllDaos(applicationContext)))
            ).create(NotificationViewModel::class.java)
            weatherViewModel.setNotificationIdToDelete(notificationId)
        }
        return Result.success()
    }
}