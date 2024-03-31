package com.example.weatherforecastapplicationkotlin.home_page.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecastapplicationkotlin.home_page.model.WeatherForeCastState
import com.example.weatherforecastapplicationkotlin.model.Clouds
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.Weather
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.model.WeatherItem
import com.example.weatherforecastapplicationkotlin.model.WeatherMain
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.model.WindWeather
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlin.math.log2


class WeatherViewModel(private var _repo : WeatherRepository, val context : Context) : ViewModel() {

    private var weatherForeCast: WeatherForeCast? = null

    private var _weatherForecast : MutableSharedFlow<WeatherForeCast> = MutableSharedFlow<WeatherForeCast>(replay = 1)
    val weatherForecast : SharedFlow<WeatherForeCast> = _weatherForecast



   // private val _rowCountStateFlow = MutableStateFlow(0) // Initial value is 0
   // val rowCountStateFlow: StateFlow<Int> = _rowCountStateFlow

    private var _weatherFromRoom = MutableStateFlow<WeatherForeCastState>(WeatherForeCastState.Loading)
    val weatherFromRoom =  _weatherFromRoom.asSharedFlow()

    private var _notificationsDate : MutableStateFlow<List<NotificationData>> = MutableStateFlow<List<NotificationData>>(emptyList())
    val notificationsDate  : StateFlow<List<NotificationData>> = _notificationsDate

    private val _notificationIdToDelete = MutableLiveData<Int>()
    val notificationIdToDelete: LiveData<Int>
        get() = _notificationIdToDelete

    private var countrySharedPreference = context.getSharedPreferences("country",Context.MODE_PRIVATE)
    init {
        getNotifiDetails()
    }

    fun setNotificationIdToDelete(notificationId: Int) {
        _notificationIdToDelete.value = notificationId
    }
    fun getWeatherForecast (lat: Double , lon: Double ,apiKey: String,units : String,lan:String ,fromFavorites : Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            val weather = _repo.getWeatherForecast(lat,lon,apiKey,units,lan)
            if(weather != null){
                if(fromFavorites){
                    Log.i("FromFavoTRUE", "getWeatherForecast: ")
                    _weatherForecast.emit(weather)
                }else{
                    Log.i("FromFavoFALSE", "getWeatherForecast: ")
                    _weatherForecast.emit(weather)
                    Log.i("beforeclear", "getWeatherForecast: ")
                    clearAll()
                    Log.i("afterClear", "getWeatherForecast: ")
                    insertHomeWeatherDetails(weather)
                    Log.i("afterInsert", "getWeatherForecast: ")
                    getHomeWeatherFromRoom()
                }
            }
        }
    }
    fun getHomeWeatherFromRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.viewAllHomeWeather()
            .catch { e->
                _weatherFromRoom.value  =  WeatherForeCastState.Failure(e)
            }
            .collect { home_weather ->
                if(home_weather != null) {
                    _weatherFromRoom.value = WeatherForeCastState.Success(home_weather)
                    Log.i(
                        "getHomeWeatherFromRoom",
                        "getHomeWeatherFromRoom: is called $home_weather ")
                }
            }
        }
    }

    fun insertHomeWeatherDetails (weatherForeCast: WeatherForeCast){
        viewModelScope.launch(Dispatchers.IO){
            _repo.insertTodayWeatherDetails(weatherForeCast)
            Log.i("insertHomeWeatherDetails", "insertHomeWeatherDetails: is called ")
        }
    }

    fun deleteHomeWeatherDetails (weatherForeCast: WeatherForeCast) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteTodayWeatherDeatils(weatherForeCast)
            getHomeWeatherFromRoom()
        }
    }

    fun getNotifiDetails () {
        viewModelScope.launch(Dispatchers.IO){
            _repo.getAllNotificationsDate().collect{
                dates -> _notificationsDate.emit(dates)
                Log.i("getNotifiDetails", "getNotifiDetails: all Dates $dates")
            }
        }
    }

    fun insertNewDate (notificationData: NotificationData){
        viewModelScope.launch(Dispatchers.IO){
            _repo.insertDate(notificationData)
        }
    }

    fun deleteOldDate (notificationData: NotificationData){
        viewModelScope.launch(Dispatchers.IO){
            _repo.deleteDate(notificationData)
            getNotifiDetails()
        }
    }

    fun clearAll () {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.ClearAllTodayWeatherDeatils()
        }
    }

    fun saveReturnCountryFromMap(country: Country) {
        with(countrySharedPreference.edit()){
            putString("cityName" , country.cityName)
            putString("countryName" , country.countryName)
            putString("countryLat",country.latitude.toString())
            putString("countryLong",country.longtuide.toString())
            apply()
        }
    }

    suspend fun returnRowCount(): Int {
        return withContext(Dispatchers.IO) {
            _repo.getRowCount()
        }
    }

    fun getCountryFromSHaredPref () : Country {
        val name = countrySharedPreference.getString("countryName","")
        val city = countrySharedPreference.getString("cityName","")
        val latString = countrySharedPreference.getString("countryLat","") ?: "0.0"
        val longString = countrySharedPreference.getString("countryLong","") ?: "0.0"
        val latitude = latString.toDoubleOrNull() ?: 0.0
        val longitude = longString.toDoubleOrNull() ?: 0.0
        Log.i("TESTCOUNTRYFROMPREFF", "getCountryFromSHaredPref: Name = $name , long = $longitude , lat=$latitude")
        return Country(
            name.toString(),
            city.toString(),
            latitude,
            longitude
        )
    }

    suspend fun getWeatherObjectFromRoom(): WeatherForeCast? {
        return  _repo.viewAllHomeWeather().firstOrNull()
    }

    fun fetchDataofWeatherForeCast(lat: Double , lon: Double , apiKey: String , units: String , lan: String ,currentDate : String) {
      viewModelScope.launch(Dispatchers.IO){
          var weatherObjectFromRoom = getWeatherObjectFromRoom()
          val dateFromRoom = getDatePart(weatherObjectFromRoom?.list?.get(0)?.dt_txt)
          val latitudeFromRoom = weatherObjectFromRoom?.city?.coord?.lat
          val longtiudeFromRoom = weatherObjectFromRoom?.city?.coord?.lon
          Log.i("weatherObjectFromRoom", "fetchDataofWeatherForeCast: $weatherObjectFromRoom , and The Date is = dateFromRoom $dateFromRoom")
          Log.i("weatherObjectFromRoom", "latitudeFromRoom: $latitudeFromRoom , and The  longtiudeFromRoom $longtiudeFromRoom")
          Log.i("weatherObjectFromRoom","latuideFromUser = $lat , and the LongtuideFromUser = $lon , and CurrentDate = $currentDate , and DateFrom $dateFromRoom")

          if(weatherObjectFromRoom == null){
              getWeatherForecast(lat,lon,apiKey,units,lan,false)
              Log.i("Inside", "fetchDataofWeatherForeCast: ")
          }else {
              val tolerance = 0.0001
              if(latitudeFromRoom != null && longtiudeFromRoom!=null){
                  if(dateFromRoom == currentDate &&
                      Math.abs(lat - latitudeFromRoom) < tolerance &&
                      Math.abs(lon - longtiudeFromRoom) < tolerance)
                      {
                          Log.i("getFromRoom", "fetchDataofWeatherForeCast: ")
                          getHomeWeatherFromRoom()
                      }else{
                      Log.i("DataChangedCall", "fetchDataofWeatherForeCast: ")
                      getWeatherForecast(lat,lon,apiKey,units,lan,false)
                      //getHomeWeatherFromRoom()
                  }
              }
          }
      }
    }

    fun getDatePart(dateTimeString: String?): String? {
        val parts = dateTimeString?.split(" ")
        return parts?.get(0)
    }



}