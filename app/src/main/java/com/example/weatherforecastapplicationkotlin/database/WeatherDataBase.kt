package com.example.weatherforecastapplicationkotlin.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecastapplicationkotlin.model.Country


@Database(entities = [Country::class], version = 1)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun getProductsDao () : IWeatherLocalDataSource
    companion object{
        @Volatile
        private var Instance : WeatherDataBase? = null

        fun getInstance (context: Context) : WeatherDataBase{
            return Instance ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext , WeatherDataBase::class.java,"products_database")
                    .build()
                Instance = instance
                instance
            }
        }
    }
}
