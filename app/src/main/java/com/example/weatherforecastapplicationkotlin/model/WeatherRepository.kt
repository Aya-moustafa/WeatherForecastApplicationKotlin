package com.example.weatherforecastapplicationkotlin.model

import android.util.Log
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.WeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_for_home_page.TodayWeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_of_notification.NotificationDeatilsLocalDataSource
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.flow.Flow

class WeatherRepository(val remoteDataSource: WeatherRemoteDataSource,val localDataSource : WeatherLocalDataSource, val homeWeatherLocalDataSource : TodayWeatherLocalDataSource , val notificationsDate : NotificationDeatilsLocalDataSource) {

    companion object {
        @Volatile
        private var instance: WeatherRepository? = null
        fun getInstance(
            remoteDataSource: WeatherRemoteDataSource ,
            localDataSource: WeatherLocalDataSource,
            weatherLocalDataSource: TodayWeatherLocalDataSource,
            notificationsDate: NotificationDeatilsLocalDataSource
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepository(remoteDataSource,localDataSource,weatherLocalDataSource,notificationsDate).also { instance = it }
            }
        }
    }

     suspend fun getWeatherForecast (lat: Double,lon: Double ,apiKey: String,units : String,lan:String) : WeatherForeCast? {
         val response = remoteDataSource.getWeatherForecastOverNetwork(lat,lon,apiKey,units,lan)
         return if(response.isSuccessful){
             Log.i("TAG", "getWeatherForeCast Repository: The responseofWeather = ${response.body()}")
             response.body()

         }else {
             Log.i("TAG", response.errorBody().toString())
             null
         }
     }

    suspend fun insertNewPlaceToFavorites (country: Country){
        localDataSource.insertPlace(country)
    }

    suspend fun deletePlaceFromFavorites (country: Country){
        localDataSource.deletePlace(country)
    }

    suspend fun viewAllFavorites() : Flow<List<Country>>{
        return localDataSource.getFavPlaces()
    }

    suspend fun viewAllHomeWeather() : Flow<WeatherForeCast> {
        return homeWeatherLocalDataSource.getHomeWeather()
    }

    suspend  fun insertTodayWeatherDetails (weatherForeCast: WeatherForeCast){
        homeWeatherLocalDataSource.insertTodayWeatherDeatils(weatherForeCast)
    }

    suspend fun deleteTodayWeatherDeatils (weatherForeCast:WeatherForeCast){
        homeWeatherLocalDataSource.deleteTodayWeatherDeatils(weatherForeCast)
    }

    suspend fun ClearAllTodayWeatherDeatils () {
        homeWeatherLocalDataSource.clearAllWeatherData()
    }

    suspend fun getRowCount() : Int {
       return homeWeatherLocalDataSource.getRowCount()
    }

    suspend fun getAllNotificationsDate() : Flow<List<NotificationData>> {
        return notificationsDate.getAllNotifiData()
    }

    suspend fun insertDate(notification : NotificationData){
        notificationsDate.insertDate(notification)
    }
    suspend fun deleteDate(notification : NotificationData){
        notificationsDate.deleteDate(notification)
    }

}