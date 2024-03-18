package com.example.weatherforecastapplicationkotlin.MainActivity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel

class SharedViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SharedViewMode::class.java)){
            SharedViewMode() as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }

    }
}