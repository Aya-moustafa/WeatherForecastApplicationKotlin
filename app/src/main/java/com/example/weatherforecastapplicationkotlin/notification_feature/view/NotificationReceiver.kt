package com.example.weatherforecastapplicationkotlin.notification_feature.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.WeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_for_home_page.TodayWeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_of_notification.NotificationDeatilsLocalDataSource
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModelFactory
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource

class NotificationReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val notificationId = intent?.getIntExtra("notificationId", -1)
            if (notificationId != null && notificationId != -1) {
                val weatherViewModel = WeatherViewModelFactory(
                    WeatherRepository.getInstance(
                        WeatherRemoteDataSource.getInstance(),
                        WeatherLocalDataSource(context),
                        TodayWeatherLocalDataSource(context),
                        NotificationDeatilsLocalDataSource(context)
                    ), context
                ).create(WeatherViewModel::class.java)
                weatherViewModel.setNotificationIdToDelete(notificationId)
            }
            showNotification(context)
        }
    }




    private fun showNotification(context: Context){
        val channelId = "default_channel_id"
        val channelName = "Default Channel"
        val notificationId = 1

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Weather Alert")
            .setContentText("")
            .setSmallIcon(R.drawable.weathericonhotoroom)

        val notification = notificationBuilder.build()
        notificationManager.notify(notificationId, notification)

    }

}