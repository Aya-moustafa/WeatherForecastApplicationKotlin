package com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places

import android.content.Context
import com.example.weatherforecastapplicationkotlin.database.data_for_home_page.WeatherDataBase
import com.example.weatherforecastapplicationkotlin.model.Country
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(context: Context)  : IWeatherLocalDataSource {
    private val dao : IWeatherLocalDataSource by lazy {
        val db : HomeDataBase = HomeDataBase.getInstance(context)
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