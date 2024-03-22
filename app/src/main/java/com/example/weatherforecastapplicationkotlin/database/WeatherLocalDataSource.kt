package com.example.weatherforecastapplicationkotlin.database

import android.content.Context
import com.example.weatherforecastapplicationkotlin.model.Country
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(context: Context) : IWeatherLocalDataSource {
    private val dao : IWeatherLocalDataSource by lazy {
        val db : WeatherDataBase = WeatherDataBase.getInstance(context)
        db.getProductsDao()
    }
    override fun getFavPlaces(): Flow<List<Country>> {
        return  dao.getFavPlaces()
    }

    override suspend fun insertPlace(country: Country) {
        dao.insertPlace(country)
    }

    override suspend fun deletePlace(country: Country) {
        dao.deletePlace(country)
    }
}