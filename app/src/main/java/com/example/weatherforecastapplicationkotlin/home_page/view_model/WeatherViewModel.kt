package com.example.weatherforecastapplicationkotlin.home_page.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


class WeatherViewModel(private var _repo : WeatherRepository, val context : Context) : ViewModel() {

    private var _weatherForecast : MutableSharedFlow<WeatherForeCast> = MutableSharedFlow<WeatherForeCast>(replay = 1)
    val weatherForecast : SharedFlow<WeatherForeCast> = _weatherForecast

   // private val _rowCountStateFlow = MutableStateFlow(0) // Initial value is 0
   // val rowCountStateFlow: StateFlow<Int> = _rowCountStateFlow

    private var _weatherFromRoom : MutableLiveData<WeatherForeCast> = MutableLiveData<WeatherForeCast>()
    val weatherFromRoom : LiveData<WeatherForeCast> = _weatherFromRoom

    private var countrySharedPreference = context.getSharedPreferences("country",Context.MODE_PRIVATE)


    fun getWeatherForecast (lat: Double , lon: Double ,apiKey: String,units : String,lan:String){
        viewModelScope.launch(Dispatchers.IO) {
            val weather = _repo.getWeatherForecast(lat,lon,apiKey,units,lan)
            if(weather != null){
                _weatherForecast.emit(weather)
            }
        }
    }

    fun getHomeWeatherFromRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.viewAllHomeWeather().collect{
             home_weather -> _weatherFromRoom.postValue(home_weather)
                Log.i("getHomeWeatherFromRoom", "getHomeWeatherFromRoom: is called $home_weather ")
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

}