package com.example.weatherforecastapplicationkotlin.home_page.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplicationkotlin.model.Weather
import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.model.WeatherResponse
import com.example.weatherforecastapplicationkotlin.model.WeatherResponseForecast
import kotlinx.coroutines.launch

class WeatherViewModel(private var _repo : WeatherRepository) : ViewModel() {
     private var _weatherForecast : MutableLiveData<WeatherForeCast> = MutableLiveData<WeatherForeCast>()
     val weatherForecast : LiveData<WeatherForeCast> = _weatherForecast

    private var  _weather : MutableLiveData<WeatherResponse> = MutableLiveData<WeatherResponse>()
    val weather : LiveData<WeatherResponse> = _weather


    fun getWeather (lat: Double,lon: Double ,apiKey: String,units : String) {
          viewModelScope.launch {
               _weather.postValue(_repo.getWeatherDetails(lat , lon , apiKey,units))
          }
    }

    fun getWeatherForecast (lat: Double , lon: Double ,apiKey: String,units : String){
        viewModelScope.launch {
            _weatherForecast.postValue(_repo.getWeatherForecast(lat,lon,apiKey,units))
        }
    }


}