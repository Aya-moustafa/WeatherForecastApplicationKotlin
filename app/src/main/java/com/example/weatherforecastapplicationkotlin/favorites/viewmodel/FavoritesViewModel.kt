package com.example.weatherforecastapplicationkotlin.favorites.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoritesViewModel(private val _irepo : WeatherRepository) : ViewModel() {
    private var _favplaces : MutableStateFlow<List<Country>> = MutableStateFlow<List<Country>>(emptyList())
    val favplaces : StateFlow<List<Country>> = _favplaces



    init {
           getLocalPlaces()
    }

   fun insertProduct (country: Country){
        viewModelScope.launch(Dispatchers.IO){
            _irepo.insertNewPlaceToFavorites(country)
        }
    }

    fun deletePlace(country: Country){
        viewModelScope.launch(Dispatchers.IO){
            _irepo.deletePlaceFromFavorites(country)
            getLocalPlaces()
        }
    }

    fun getLocalPlaces (){
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.viewAllFavorites().collect{
                    place -> _favplaces.value=place
            }
        }
    }

}