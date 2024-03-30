package com.example.weatherforecastapplicationkotlin.database.data_of_notification

import android.content.Context
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.HomeDataBase
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.IWeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.flow.Flow

class NotificationDeatilsLocalDataSource(context: Context) : INotificationDeatilsLocalDataSource {
    private val dao : INotificationDeatilsLocalDataSource by lazy {
        val db : HomeDataBase = HomeDataBase.getInstance(context)
        db.getNotDateDao()
    }
    override fun getAllNotifiData(): Flow<List<NotificationData>> {
       return dao.getAllNotifiData()
    }

    override suspend fun insertDate(notification: NotificationData) {
       dao.insertDate(notification)
    }

    override suspend fun deleteDate(notification: NotificationData) {
        dao.deleteDate(notification)
    }
}