package com.example.weatherforecastapplicationkotlin.database.data_for_home_page

import androidx.room.TypeConverter
import com.example.weatherforecastapplicationkotlin.model.City
import com.example.weatherforecastapplicationkotlin.model.Weather
import com.example.weatherforecastapplicationkotlin.model.WeatherItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromList(list: List<WeatherItem>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toList(json: String): List<WeatherItem> {
        val listType = object : TypeToken<List<WeatherItem>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    fun fromCity(city: City): String {
        return Gson().toJson(city)
    }

    @TypeConverter
    fun toCity(json: String): City {
        return Gson().fromJson(json, City::class.java)
    }



    @TypeConverter
    fun fromWeather(weather: Weather): String {
        return Gson().toJson(weather)
    }

    @TypeConverter
    fun toWeather(json: String): Weather {
        return Gson().fromJson(json, Weather::class.java)
    }

}