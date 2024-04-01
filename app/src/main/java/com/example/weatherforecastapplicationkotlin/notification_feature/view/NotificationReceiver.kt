package com.example.weatherforecastapplicationkotlin.notification_feature.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.LocalDataSource
import com.example.weatherforecastapplicationkotlin.model.AllDaos
import com.example.weatherforecastapplicationkotlin.model.repository.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.notification_feature.NotifViewModel.NotificationViewModel
import com.example.weatherforecastapplicationkotlin.notification_feature.NotifViewModel.NotificationViewModelFactory


class NotificationReceiver() : BroadcastReceiver() {
    lateinit var  notfViewModel : NotificationViewModel
    lateinit var  notifFactory  : NotificationViewModelFactory
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val description = intent.getStringExtra("description")
            val city = intent.getStringExtra("cityname")
            val date = intent.getStringExtra("date")
            val hour = intent.getStringExtra("hour")
            Log.i("NotificationReceiver", "onReceive:  All Data : $city , $description , $date , $hour")
            if (!description.isNullOrEmpty() && city!=null && date != null && hour!=null) {
                showNotification(context, description,city)
                val weatherViewModel = NotificationViewModelFactory(
                    WeatherRepository.getInstance(
                        WeatherRemoteDataSource.getInstance() , LocalDataSource(AllDaos(context)))
                ).create(NotificationViewModel::class.java)
                weatherViewModel.deleteNotifiByDate(date,hour)
            }
        }
    }

    private fun showNotification(context: Context , desc : String , city  :String){
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
            .setContentText("The Weather Today in $city : $desc")
            .setSmallIcon(R.drawable.weathericonhotoroom)

        val notification = notificationBuilder.build()
        notificationManager.notify(notificationId, notification)

    }

}