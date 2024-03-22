package com.example.weatherforecastapplicationkotlin.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecastapplicationkotlin.model.Country
import kotlinx.coroutines.flow.Flow

@Dao
interface IWeatherLocalDataSource {
    @Query("SELECT * FROM Fav_Places")
    fun getFavPlaces () : Flow<List<Country>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlace (country: Country)
    @Delete
    suspend fun deletePlace (country: Country)
}