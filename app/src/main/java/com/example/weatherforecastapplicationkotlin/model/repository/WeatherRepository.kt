package com.example.weatherforecastapplicationkotlin.model.repository

import android.util.Log
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.ILocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.LocalDataSource
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.network.IWeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.flow.Flow

class WeatherRepository(val remoteDataSource: IWeatherRemoteDataSource, val localDataSource : ILocalDataSource) :
    IWeatherRepository {

    companion object {
        @Volatile
        private var instance: WeatherRepository? = null
        fun getInstance(
            remoteDataSource: IWeatherRemoteDataSource,
            localDataSource: ILocalDataSource,
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepository(remoteDataSource,localDataSource).also { instance = it }
            }
        }

    }

     override suspend fun getWeatherForecast (lat: Double, lon: Double, apiKey: String, units : String, lan:String) : WeatherForeCast? {
         val response = remoteDataSource.getWeatherForecastOverNetwork(lat,lon,apiKey,units,lan)
         return if(response.isSuccessful){
             Log.i("TAG", "getWeatherForeCast Repository: The responseofWeather = ${response.body()}")
             response.body()

         }else {
             Log.i("TAG", response.errorBody().toString())
             null
         }
     }

    override suspend fun insertNewPlaceToFavorites (country: Country){
        localDataSource.insertPlace(country)
    }

    override suspend fun deletePlaceFromFavorites (country: Country){
        localDataSource.deletePlace(country)
    }

    override suspend fun viewAllFavorites() : Flow<List<Country>>{
        return localDataSource.getFavPlaces()
    }

    override suspend fun viewAllHomeWeather() : Flow<WeatherForeCast> {
        return localDataSource.getHomeWeather()
    }

    override suspend  fun insertTodayWeatherDetails (weatherForeCast: WeatherForeCast){
        localDataSource.insertTodayWeatherDeatils(weatherForeCast)
    }

    override suspend fun deleteTodayWeatherDeatils (weatherForeCast: WeatherForeCast){
        localDataSource.deleteTodayWeatherDeatils(weatherForeCast)
    }

    override suspend fun ClearAllTodayWeatherDeatils () {
        localDataSource.clearAllWeatherData()
    }

    override suspend fun getRowCount() : Int {
       return localDataSource.getRowCount()
    }

    override suspend fun getAllNotificationsDate() : Flow<List<NotificationData>> {
        return localDataSource.getAllNotifiData()
    }

    override suspend fun insertDate(notification : NotificationData){
        localDataSource.insertDate(notification)
    }
    override suspend fun deleteDate(notification : NotificationData){
        localDataSource.deleteDate(notification)
    }

    override suspend fun deleteNotifiByDate(datetime: String, hourTime: String) {
        localDataSource.deleteSpcDate(datetime , hourTime)
    }

}