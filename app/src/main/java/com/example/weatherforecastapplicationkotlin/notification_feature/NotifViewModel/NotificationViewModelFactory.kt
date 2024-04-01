package com.example.weatherforecastapplicationkotlin.notification_feature.NotifViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.model.repository.WeatherRepository

class NotificationViewModelFactory(private val _irepo : WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NotificationViewModel::class.java)){
            NotificationViewModel(_irepo) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }

    }
}