package com.example.weatherforecastapplicationkotlin.MainActivity

const val API_KEY = "7c354c3478d8fae96d35bfa38a22db2c"
var isSearchOnMapOpenedFromSetting = false
var isMapSwitchChecked = false

enum class AddressComponentType {
    CITY,
    COUNTRY,
    UNKNOWN
}