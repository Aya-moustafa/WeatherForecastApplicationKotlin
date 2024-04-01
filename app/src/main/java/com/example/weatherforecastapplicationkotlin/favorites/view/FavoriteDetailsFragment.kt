package com.example.weatherforecastapplicationkotlin.favorites.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecastapplicationkotlin.MainActivity.API_KEY
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.ILocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.LocalDataSource
import com.example.weatherforecastapplicationkotlin.home_page.view.WeeklyForecastListAdapter
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModelFactory
import com.example.weatherforecastapplicationkotlin.model.AllDaos
import com.example.weatherforecastapplicationkotlin.model.Clouds
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.Weather
import com.example.weatherforecastapplicationkotlin.model.WeatherItem
import com.example.weatherforecastapplicationkotlin.model.WeatherMain
import com.example.weatherforecastapplicationkotlin.model.repository.WeatherRepository
import com.example.weatherforecastapplicationkotlin.model.WindWeather
import com.example.weatherforecastapplicationkotlin.network.IWeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.setting.model.SettingOptions
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewMode
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewModelFactory
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FavoriteDetailsFragment : Fragment() {
    private final var TAG :String = "FavoriateFrgamentDetails"
    private lateinit var translateArabic : Translator
    private lateinit var favyourLocation : TextView
    private lateinit var favtemp : TextView
    private lateinit var favfeelsLike : TextView
    private lateinit var favdesc : TextView
    private lateinit var favdate : TextView
    private lateinit var favwindTV : TextView
    private lateinit var favcloudTV : TextView
    private lateinit var favhumidityTV : TextView
    private lateinit var favUVTV : TextView
    private lateinit var favvisibTV : TextView
    private lateinit var favpresstureTV : TextView
    private lateinit var favweekly_forecast : RecyclerView
    private lateinit var favcurrent_temp_img : ImageView
    lateinit  var weatherViewModel: WeatherViewModel
    lateinit var  weatherFactory: WeatherViewModelFactory
    lateinit  var settingViewModel: SettingViewMode
    lateinit var  adapter : WeeklyForecastListAdapter
    lateinit var  sharedFactory: SettingViewModelFactory
    var  unitTemp  : String = "standard"
    var  windSpeed : String = "Meter/Sec"
    var  language  : String = "en"
    var  locationSett  : String = "gps"
    var todayDate : String = ""
    var dateFromApiFav : String = ""
    var countryFromApiFav : String = ""
    var fromFavorite : Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val country = arguments?.getSerializable("fav_country") as Country?
        var settingOptions = arguments?.getSerializable("setting_data") as SettingOptions?
        downloadModelToTranslate()
        if (country != null && settingOptions != null) {
            Log.i(
                TAG,
                "onViewCreatedFIRST: ${country?.countryName} ${country?.cityName} , ${country.latitude}"
            )
            weatherFactory = WeatherViewModelFactory(WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance() , LocalDataSource(AllDaos(requireContext()))) , requireContext())
            weatherViewModel =
                ViewModelProvider(this, weatherFactory).get(WeatherViewModel::class.java)
            sharedFactory = SettingViewModelFactory(requireActivity().application)
            settingViewModel =
                ViewModelProvider(this, sharedFactory).get(SettingViewMode::class.java)
           // settingViewModel.emitChangingSetting()
            favcurrent_temp_img = view.findViewById(R.id.favcurrentTempImg)
            favyourLocation = view.findViewById(R.id.favloc)
            favtemp = view.findViewById(R.id.favtempTxt)
            favfeelsLike = view.findViewById(R.id.favfeelsLike)
            favdesc = view.findViewById(R.id.favdescTxt)
            favdate = view.findViewById(R.id.favdateTxt)
            favwindTV = view.findViewById(R.id.favwindTxt)
            favcloudTV = view.findViewById(R.id.favcloudTxt)
            favhumidityTV = view.findViewById(R.id.favhumdTxt)
            favpresstureTV = view.findViewById(R.id.favpressTxt)
            favUVTV = view.findViewById(R.id.favuvTxt)
            favvisibTV = view.findViewById(R.id.favvisiTxt)
            favweekly_forecast = view.findViewById(R.id.favweekly_forecast_recycle)
            val layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
            adapter = WeeklyForecastListAdapter(requireContext(), settingOptions)
            Log.i(TAG, "settingInAdapte: $settingOptions ")
            favweekly_forecast.adapter = adapter
            favweekly_forecast.layoutManager = layoutManager
            lifecycleScope.launch {
                weatherViewModel.weatherForecast.collect { weatherForecast ->
                    Log.i(TAG, "onViewCreated: The Returned Data From Api : ${weatherForecast}")
                    Log.i(TAG, "onViewCreated: The Returned city : ${weatherForecast.city.name}")
                    Log.i(
                        TAG,
                        "onViewCreated: The Current Day of List of 5 Days : ${
                            weatherForecast.list.get(0)
                        }"
                    )
                    dateFromApiFav = getDatePart(weatherForecast.list.get(0).dt_txt)
                    countryFromApiFav = weatherForecast.city.name
                    Log.i(TAG, "onViewCreated: countryFromRoom = $countryFromApiFav")
                    Log.i(TAG, "onViewCreated:  The Returned Data From Room is ${dateFromApiFav} ")
                    var today = weatherForecast.list.get(0)
                    var main: WeatherMain = weatherForecast.list.get(0).main
                    if (language == "en") {
                        favyourLocation.text = country.countryName
                    } else if (language == "ar") {
                        translateCityName(country.countryName) { translatedCityName ->
                            favyourLocation.text = translatedCityName
                            //    yourLocation.invalidate()
                            Log.i(
                                TAG,
                                "onViewCreated: the returned city when Ar : $translatedCityName"
                            )
                        }

                    }
                    favtemp.text = today.getTemperatureInInt().toString()
                    favfeelsLike.text = main.feels_like.toString()
                    var weather: Weather = today.weather.get(0)
                    val formattedDate = formatDate(weatherForecast.list.get(0).dt)
                    if (language == "en") {
                        favdesc.text = weather.description
                        favdate.text = formattedDate
                    } else if (language == "ar") {
                        favdesc.text = weather.description
                        translateCityName(formattedDate) { translatedCityName ->
                            favdate.text = translatedCityName
                            Log.i(
                                TAG,
                                "onViewCreated: the returned city when Ar : $translatedCityName"
                            )
                        }

                    }


                    var wind: WindWeather = today.wind
                    Log.i("wind", "onViewCreated:${wind} ")
                    if (windSpeed == "Meter/Sec") {
                        var formattedWind = formatWindInMeterPerSec(wind)
                        favwindTV.text = formattedWind + "m/s"
                    } else if (windSpeed == "Mile/Hour") {
                        var formattedWind = formatWindInMilesPerHour(wind)
                        favwindTV.text = formattedWind + "mph"
                    }
                    var clouds: Clouds = today.clouds
                    var formatedClouds = formatCloudiness(clouds)
                    favcloudTV.text = formatedClouds
                    var formattedHumidity = formatHumidity(main)
                    favhumidityTV.text = formattedHumidity
                    var formattedPressure = formatPressure(main)
                    favpresstureTV.text = formattedPressure
                    var UV = today.sys.pod
                    favUVTV.text = UV
                    favvisibTV.text = today.visibility.toString() + " m"
                    val url_img = "https://openweathermap.org/img/wn/" + weather.icon + ".png"
                    Glide.with(this@FavoriteDetailsFragment).load(url_img)
                        .apply(
                            RequestOptions()
                            //  .placeholder(R.drawable.placeholder) // don't forget the placeholder image
                            //  .error(R.drawable.placeholder)
                        )
                        .into(favcurrent_temp_img)
                    val filteredList = mutableListOf<WeatherItem>()

                    var isFirstDay = true
                    var previousDate = ""
                    weatherForecast.list.forEach { weatherData ->
                        val currentDate =
                            weatherData.dt_txt.substring(0, 10) // Extracting date without time
                        val currentTime = weatherData.dt_txt.substring(11) // Extracting time

                        // Exclude the first day
                        if (isFirstDay) {
                            isFirstDay = false
                            previousDate = currentDate
                            return@forEach
                        }

                        // Add data entry if time is 00:00:00 or it's a new day
                        if (currentTime == "00:00:00" || currentDate != previousDate) {
                            filteredList.add(weatherData)
                            previousDate = currentDate

                        }
                    }
                    Log.i(TAG, "adapter2: ")
                    adapter.submitList(filteredList)
                    Log.i(
                        TAG,
                        "onViewCreated: The Weather Forecast For 5 days left is: $filteredList"
                    )
                }


            }
            lifecycleScope.launch {
                settingViewModel.settingsFlow.collectLatest { setting ->
                    unitTemp = setting.unitsTemp
                    language = setting.language
                    locationSett = setting.location
                    windSpeed = setting.windSpeed
                    Log.i(TAG, "adapter: ")
                    lifecycleScope.launch(Dispatchers.Main) {
                        when (language) {
                            "ar" -> {
                                // If the language is English, translate the city name to Arabic
                                // translateCityName(country.countryName) { translatedCityName ->
                                // Update the location text view with the translated city name
                                // yourLocation.text = translatedCityName
                                //    yourLocation.invalidate()
                                // Log.i(TAG, "onViewCreated: the returned city when Ar : $translatedCityName")
                                //}
                            }

                            "en" -> {
                                //   yourLocation.text = country.countryName
                                //  Log.i(TAG, "onViewCreated: the returned city when en : ${country.countryName}")
                            }
                        }
                    }
                }
            }
            weatherViewModel.getWeatherForecast(
                country.latitude,
                country.longtuide,
                API_KEY,
                unitTemp,
                language,
                fromFavorite
            )
            Log.i(
                "TESTTTTTTTTTTTTTT",
                "onViewCreated: country.latitude = $country.latitude , $country.longtuide"
            )
        }
    }
    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: ")
    }
    private fun downloadModelToTranslate() {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.ARABIC)
            .build()
        translateArabic = Translation.getClient(options)
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        translateArabic.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                // ...
            }

    }

    private fun translateCityName(cityName: String, onSuccess: (String) -> Unit) {
        translateArabic.translate(cityName)
            .addOnSuccessListener { translatedText ->
                onSuccess(translatedText)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Translation failed with exception: ${exception.message}")
            }
    }
    fun getDatePart(dateTimeString: String): String {
        val parts = dateTimeString.split(" ")
        return parts[0]
    }
    fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp * 1000 // Convert seconds to milliseconds
        }
        val dateFormat = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    fun formatWindInMeterPerSec(wind: WindWeather): String {
        return "${String.format("%.2f", wind.speed)} m/s"
    }
    fun convertMetersPerSecondToMilesPerHour(mps: Double): Double {
        return mps * 2.23694
    }
    fun formatWindInMilesPerHour(wind: WindWeather): String {
        val speedInMetersPerSecond = wind.speed
        val speedInMilesPerHour = convertMetersPerSecondToMilesPerHour(speedInMetersPerSecond)
        return "${String.format("%.2f", speedInMilesPerHour)} mph"
    }
    fun formatPressure(main : WeatherMain): String {
        return "${main.pressure} hPa"
    }

    fun formatHumidity(main: WeatherMain): String {
        return "${main.humidity}%"
    }
    fun formatCloudiness(clouds: Clouds): String {
        return "${clouds.all}%"
    }
}