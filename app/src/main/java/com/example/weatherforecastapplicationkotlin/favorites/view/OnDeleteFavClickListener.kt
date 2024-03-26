package com.example.weatherforecastapplicationkotlin.favorites.view

import com.example.weatherforecastapplicationkotlin.model.Country

interface OnDeleteFavClickListener {
    fun onClicktodelete(country: Country)
}