package com.example.weatherforecastapplicationkotlin.notification_feature

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weatherforecastapplicationkotlin.R

class NotificationReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        if (context != null) {
            showNotification(context)
        }
    }

    companion object{
        var descriptionOfWeather : String = ""
        fun getDescriptionOfWeather (desc : String) : String {
             descriptionOfWeather = desc
            return descriptionOfWeather
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