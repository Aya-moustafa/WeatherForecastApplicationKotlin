package com.example.weatherforecastapplicationkotlin.setting.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingViewMode::class.java)){
            SettingViewMode(application) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }

    }
}