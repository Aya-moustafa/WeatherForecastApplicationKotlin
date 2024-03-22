package com.example.weatherforecastapplicationkotlin.home_page.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplicationkotlin.model.Weather
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.model.WeatherResponse
import com.example.weatherforecastapplicationkotlin.model.WeatherResponseForecast
import com.example.weatherforecastapplicationkotlin.setting.model.SettingOptions
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.log

class WeatherViewModel(private var _repo : WeatherRepository, val context : Context) : ViewModel() {
    private var hasEmittedSettings = false

    private var _weatherForecast : MutableLiveData<WeatherForeCast> = MutableLiveData<WeatherForeCast>()
     val weatherForecast : LiveData<WeatherForeCast> = _weatherForecast

    private var  _weather : MutableLiveData<WeatherResponse> = MutableLiveData<WeatherResponse>()
    val weather : LiveData<WeatherResponse> = _weather

    private val settingsSharedPreference = context.getSharedPreferences("Setting", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableSharedFlow<SettingOptions>(replay = 1)
    val settingsFlow: SharedFlow<SettingOptions> = _settingsFlow


    init {

    }

    fun getWeather (lat: Double,lon: Double ,apiKey: String,units : String) {
        Log.i("TAG", "getWeather: ")
          viewModelScope.launch(Dispatchers.IO) {
               _weather.postValue(_repo.getWeatherDetails(lat , lon , apiKey,units))
              Log.i("TAG", "getWeather: in launch")
          }
    }

    fun getWeatherForecast (lat: Double , lon: Double ,apiKey: String,units : String){
        viewModelScope.launch(Dispatchers.IO) {
            _weatherForecast.postValue(_repo.getWeatherForecast(lat,lon,apiKey,units))
        }
    }

    fun emitChangingSetting () {
        val updatedSetting = getSettingDataFromSP()
            Log.i("Init", "inInit Scope: $updatedSetting ")
            viewModelScope.launch {
                _settingsFlow.emit(updatedSetting)
        }
    }
    fun getSettingDataFromSP () : SettingOptions{
        return SettingOptions(
            settingsSharedPreference.getString("selectedTempertureUnit","") ?: "",
            settingsSharedPreference.getString("selectedWindSpeed","") ?: "",
            settingsSharedPreference.getString("selectedLocation","") ?: "",
            settingsSharedPreference.getString("selectedLanguage","") ?: ""
        )
    }

  /*  fun toObserveSettingChange ()  {
        viewModelScope.launch {
            try{
                settingsFlow.collect{
                        setting ->
                    Log.i("toObserveSettingChange", "collectSettingsData: $setting")
                }
            }catch (e: Exception) {
                // Handle the exception here
                Log.e("toObserveSettingChange", "Error occurred: ${e.message}")
            }
        }

    }*/

}