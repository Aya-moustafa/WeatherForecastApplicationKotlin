package com.example.weatherforecastapplicationkotlin.model

data class WeatherForeCast(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherItem>,
    val city: City
)

data class WeatherItem(
    val dt: Long,
    val main: WeatherMain,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: WindWeather,
    val visibility: Int,
    val pop: Double,
    val sys: Syst,
    val dt_txt: String
) {
    fun getTemperature(): Int {
        val temperatureCelsius = main.temp - 273.15
        return temperatureCelsius.toInt()
    }
}

data class WeatherMain(
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

data class Weather(
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

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)
