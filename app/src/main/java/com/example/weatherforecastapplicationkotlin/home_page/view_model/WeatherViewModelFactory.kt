package com.example.weatherforecastapplicationkotlin.home_page.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository

class WeatherViewModelFactory(private val _irepo : WeatherRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WeatherViewModel::class.java)){
            WeatherViewModel(_irepo) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }

    }
}