package com.example.weatherforecastapplicationkotlin.database.data_for_home_page


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast

@TypeConverters(Converters::class)
@Database(entities = [WeatherForeCast::class], version =6)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun getTodayWeatherDao () : ITodayWeatherLocalDataBase

    companion object{
        @Volatile
        private var Instance : WeatherDataBase? = null

        fun getInstance (context: Context) : WeatherDataBase {
            return Instance ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext , WeatherDataBase::class.java,"products_database")
                    .fallbackToDestructiveMigration()
                    .build()
                Instance = instance
                instance
            }
        }
    }
}
