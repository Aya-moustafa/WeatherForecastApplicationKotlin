package com.example.weatherforecastapplicationkotlin.home_page.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplicationkotlin.MainActivity.isMapSwitchChecked
import com.example.weatherforecastapplicationkotlin.model.Country
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

    private var _weatherForecast : MutableLiveData<WeatherForeCast> = MutableLiveData<WeatherForeCast>()
     val weatherForecast : LiveData<WeatherForeCast> = _weatherForecast

    private var  _weather : MutableLiveData<WeatherResponse> = MutableLiveData<WeatherResponse>()
    val weather : LiveData<WeatherResponse> = _weather

    private var countrySharedPreference = context.getSharedPreferences("country",Context.MODE_PRIVATE)
    private val settingsSharedPreference = context.getSharedPreferences("Setting", Context.MODE_PRIVATE)




    init {

    }

    fun getWeather (lat: Double,lon: Double ,apiKey: String,units : String,lan:String) {
        Log.i("TAG", "getWeather: ")
          viewModelScope.launch(Dispatchers.IO) {
               _weather.postValue(_repo.getWeatherDetails(lat , lon , apiKey,units,lan))
              Log.i("TAG", "getWeather: in launch")
          }
    }

    fun getWeatherForecast (lat: Double , lon: Double ,apiKey: String,units : String,lan:String){
        viewModelScope.launch(Dispatchers.IO) {
            _weatherForecast.postValue(_repo.getWeatherForecast(lat,lon,apiKey,units,lan))
        }
    }

   /* fun emitChangingSetting () {
        val updatedSetting = getSettingDataFromSP()
        Log.i("Init", "inInit Scope: $updatedSetting ")
            viewModelScope.launch {
                _settingsFlow.emit(updatedSetting)
        }
    }
    fun getSettingDataFromSP () : SettingOptions{
        return SettingOptions(
            settingsSharedPreference.getString("selectedTempertureUnit","") ?: "",
            settingsSharedPreference.getString("selectedWindSpeed","") ?: "Meter/Sec",
            settingsSharedPreference.getString("selectedLanguage","") ?: "",
            settingsSharedPreference.getString("selectedLocation","") ?: ""
        )
    }
*/
    fun saveReturnCountryFromMap(country: Country) {
        with(countrySharedPreference.edit()){
            putString("countryName" , country.countryName)
            putString("countryLat",country.latitude.toString())
            putString("countryLong",country.longtuide.toString())
            apply()
        }
    }

    fun getCountryFromSHaredPref () : Country {
        val name = countrySharedPreference.getString("countryName","")
        val latString = countrySharedPreference.getString("countryLat","") ?: "0.0"
        val longString = countrySharedPreference.getString("countryLong","") ?: "0.0"
        val latitude = latString.toDoubleOrNull() ?: 0.0
        val longitude = longString.toDoubleOrNull() ?: 0.0
        Log.i("TESTCOUNTRYFROMPREFF", "getCountryFromSHaredPref: Name = $name , long = $longitude , lat=$latitude")
        return Country(
            name.toString(),
            latitude,
            longitude
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