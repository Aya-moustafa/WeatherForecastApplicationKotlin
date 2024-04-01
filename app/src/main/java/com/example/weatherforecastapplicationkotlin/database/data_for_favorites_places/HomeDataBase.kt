package com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecastapplicationkotlin.database.data_of_notification.INotificationDeatilsLocalDataSource
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData

@Database(entities = [Country::class , NotificationData::class], version =4)
abstract class HomeDataBase  : RoomDatabase() {
    abstract fun getFavoritesDao () : IWeatherLocalDataSource
    abstract fun getNotDateDao () : INotificationDeatilsLocalDataSource

    companion object{
        @Volatile
        private var Instance : HomeDataBase? = null

        fun getInstance (context: Context) : HomeDataBase {
            return Instance ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext , HomeDataBase::class.java,"favorites_places_database")
                    .fallbackToDestructiveMigration()
                    .build()
                Instance = instance
                instance
            }
        }
    }
}
