package com.example.weatherforecastapplicationkotlin.home_page.model

import com.example.weatherforecastapplicationkotlin.model.WeatherForeCast

sealed class WeatherForeCastState {
    class Success(val data : WeatherForeCast) :WeatherForeCastState()
    class Failure(val errormsg : Throwable) :WeatherForeCastState()
    object Loading  :WeatherForeCastState()
}