package com.example.weatherforecastapplicationkotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
@Entity(tableName = "home_weather_details")
data class WeatherForeCast(
    @NotNull
    @PrimaryKey
    val cod: String,
    val message: Int,
    @NotNull
    val cnt: Int,
    @NotNull
    val list: List<WeatherItem>,
    val city: City
)
@Entity(tableName = "home_weather_item")
data class WeatherItem(
    @NotNull
    @PrimaryKey
    val dt: Long,
    @NotNull
    val main: WeatherMain,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: WindWeather,
    @NotNull
    val visibility: Int,
    val pop: Double,
    val sys: Syst,
    @NotNull
    val dt_txt: String
){
    fun getTemperatureInInt(): Int {
        val temperatureCelsius = main.temp
        return temperatureCelsius.toInt()
    }
    fun getTempFeelsLikeInInt(): Int {
        val temperatureCelsius = main.feels_like
        return temperatureCelsius.toInt()
    }
}
data class WeatherMain(
    val id_main: Int = 0,
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)
@Entity(tableName = "weather")
data class Weather(
    @NotNull
    @PrimaryKey
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class WindWeather(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Syst(
    val pod: String
)
@Entity(tableName = "city_details")
data class City(
    @NotNull
    @PrimaryKey
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class Coord(
    val lon: Double,
    val lat: Double
)
data class Clouds(
    val all: Int
)