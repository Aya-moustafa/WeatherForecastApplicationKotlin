package com.example.weatherforecastapplicationkotlin.database.data_for_home_page

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import kotlinx.coroutines.flow.Flow

@Dao
interface ITodayWeatherLocalDataBase {

    @Query("SELECT * FROM home_weather_details")
    fun getHomeWeather () : Flow<WeatherForeCast>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodayWeatherDeatils (weatherForeCast:WeatherForeCast)
    @Delete
    suspend fun deleteTodayWeatherDeatils (weatherForeCast: WeatherForeCast)

    @Query("DELETE FROM home_weather_details")
    suspend fun clearAllWeatherData()

    @Query("SELECT COUNT(*) FROM home_weather_details")
    fun getRowCount(): Int

}