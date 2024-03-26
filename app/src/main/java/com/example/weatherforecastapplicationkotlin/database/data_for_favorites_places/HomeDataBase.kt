package com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecastapplicationkotlin.database.data_for_home_page.ITodayWeatherLocalDataBase
import com.example.weatherforecastapplicationkotlin.model.Country

@Database(entities = [Country::class], version =3)
abstract class HomeDataBase  : RoomDatabase() {
    abstract fun getProductsDao () : IWeatherLocalDataSource
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
