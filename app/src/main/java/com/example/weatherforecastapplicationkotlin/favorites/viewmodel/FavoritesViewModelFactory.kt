package com.example.weatherforecastapplicationkotlin.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapplicationkotlin.model.repository.WeatherRepository

class FavoritesViewModelFactory (private val _irepo : WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)){
            FavoritesViewModel(_irepo) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }

    }
}