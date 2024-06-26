package com.example.weatherforecastapplicationkotlin.home_page.view


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecastapplicationkotlin.MainActivity.API_KEY
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewMode
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewModelFactory
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.LocalDataSource
import com.example.weatherforecastapplicationkotlin.home_page.model.WeatherForeCastState
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
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

const val REQUEST_LOCATION_CODE = 2005

class HomeFragment : Fragment() {
    private lateinit var translateArabic : Translator
    private lateinit var yourLocation : TextView
    private lateinit var temp : TextView
    private lateinit var feelsLike : TextView
    private lateinit var desc : TextView
    private lateinit var date : TextView
    private lateinit var windTV : TextView
    private lateinit var cloudTV : TextView
    private lateinit var humidityTV : TextView
    private lateinit var UVTV : TextView
    private lateinit var visibTV : TextView
    private lateinit var presstureTV : TextView
    private lateinit var weekly_forecast : RecyclerView
    private lateinit var houlry_forecast : RecyclerView
    private lateinit var current_temp_img : ImageView
    lateinit var  adapter : WeeklyForecastListAdapter
    lateinit var  houradapter: HourlyWeatherListAdapter
    var  rowCount: Int = 0
    // private lateinit var weekly_forecast_List : List<WeatherItem>

    private final var TAG :String = "HomeFrag"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_CODE = 1001 // Your request code
    private  var longitude: Double = 0.0
    private  var latitude: Double  = 0.0
    private  var longitudeFromSett: Double = 0.0
    private  var latitudeFromSett: Double  = 0.0
    lateinit  var weatherViewModel: WeatherViewModel
    lateinit var  weatherFactory: WeatherViewModelFactory
    lateinit  var settingViewModel: SettingViewMode
    lateinit var  sharedFactory: SettingViewModelFactory
     var  unitTemp  : String = "standard"
     var  windSpeed : String = "Meter/Sec"
     var  language  : String = "en"
     var  locationSett  : String = "gps"
     var todayDate : String = ""
     var dateFromRoom : String = ""
     var countryFromRoom : String = ""
     var countryFromApi : String = ""
     private val PREF_KEY = "country_from_api"
    lateinit var  progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView: ")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object{
        fun formatDate(timestamp: Long): String {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp * 1000 // Convert seconds to milliseconds
            }
            val dateFormat = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        current_temp_img = view.findViewById(R.id.currentTempImg)
        yourLocation = view.findViewById(R.id.tess)
        temp         = view.findViewById(R.id.tempTxt)
        feelsLike    = view.findViewById(R.id.feelsLike)
        desc         = view.findViewById(R.id.descTxt)
        date         = view.findViewById(R.id.dateTxt)
        windTV       = view.findViewById(R.id.windTxt)
        cloudTV      = view.findViewById(R.id.cloudTxt)
        humidityTV   = view.findViewById(R.id.humdTxt)
        presstureTV  = view.findViewById(R.id.pressTxt)
        UVTV         = view.findViewById(R.id.uvTxt)
        visibTV      = view.findViewById(R.id.visiTxt)
        weekly_forecast = view.findViewById(R.id.weekly_forecast_recycle)
        houlry_forecast = view.findViewById(R.id.hourly_recycler_view)
        progressBar  = view.findViewById(R.id.progressBar)

        if (isLocationEnabled()) {
            if (checkPermissions()) {
                getFreshLocation()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
            }
        } else {
            enableLocationServices()
        }
        weatherFactory = WeatherViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance() , LocalDataSource(AllDaos(requireContext()))),requireContext())
        weatherViewModel = ViewModelProvider(this,weatherFactory).get(WeatherViewModel::class.java)
        sharedFactory = SettingViewModelFactory(requireActivity().application)
        settingViewModel = ViewModelProvider(this,sharedFactory).get(SettingViewMode::class.java)
        //val layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        //adapter = WeeklyForecastListAdapter(requireContext())
        //weekly_forecast.adapter = adapter
        //weekly_forecast.layoutManager = layoutManager
        todayDate = getCurrentDateTimeFormatted()
        Log.i(TAG, "onViewCreated: Current Date is $todayDate")
        arguments?.let { args ->
            val country = args.getSerializable("country") as? Country
            if (country != null) {
                weatherViewModel.saveReturnCountryFromMap(country)
                Log.i(TAG, "onViewCreated: The Returned country from the Setting fragment is be : ${country.countryName}")
            }
        }
        downloadModelToTranslate()
        val country =weatherViewModel.getCountryFromSHaredPref()
        longitudeFromSett = country.longtuide
        latitudeFromSett  = country.latitude
       // countryName       = country.countryName
       // setLocationNameInUI()
        lifecycleScope.launch {
            weatherViewModel.weatherForecast.collect{ weatherForecast ->
                Log.i(TAG, "onViewCreated: The Returned Data From Api : ${weatherForecast}")
                Log.i(TAG, "onViewCreated: The Returned city : ${weatherForecast.city.name}")

                weatherForecast?.let {
                    Log.i(
                        TAG,
                        "onViewCreated: The Current Day of List of 5 Days : ${it.list.get(0)}"
                    )
                    countryFromApi = it.city.name
                    saveStringToSharedPreferences(countryFromApi)
                  //  weatherViewModel.getHomeWeatherFromRoom()
                    Log.i(TAG, "onViewCreated:countryFro//mApi = $countryFromApi ")
                }
            }

        }

       lifecycleScope.launch {
           repeatOnLifecycle(Lifecycle.State.STARTED) {
               weatherViewModel.weatherFromRoom.collect{
                   room_data ->
                       when(room_data){
                           is WeatherForeCastState.Loading -> {
                               progressBar.visibility = View.VISIBLE
                               weekly_forecast.visibility= View.GONE
                           }is WeatherForeCastState.Success -> {
                               progressBar.visibility = View.GONE
                               weekly_forecast.visibility= View.VISIBLE
                               Log.i(TAG, "onLocationResult: TESTTTTTTTTTT WHICH FIRST 1")
                               dateFromRoom = getDatePart(room_data.data.list.get(0).dt_txt)
                               countryFromRoom = room_data.data.city.name
                               Log.i(TAG, "onViewCreated: countryFromRoom = $countryFromRoom")
                               Log.i(TAG, "onViewCreated:  The Returned Data From Room is ${dateFromRoom} ")
                               var today = room_data.data.list.get(0)
                               var main: WeatherMain = room_data.data.list.get(0).main
                                if(language == "en") {
                               yourLocation.text = room_data.data.city.name
                                }else if (language == "ar") {
                                    translateCityName(room_data.data.city.name) { translatedCityName ->
                                        yourLocation.text = translatedCityName
                                        //    yourLocation.invalidate()
                                         Log.i(TAG, "onViewCreated: the returned city when Ar : $translatedCityName")
                                    }

                                }else {
                                    yourLocation.text = room_data.data.city.name
                                }
                               if(unitTemp == "standard"){
                                   temp.text = today.getTemperatureInInt().toString()
                                   feelsLike.text = main.feels_like.toString()
                               }else if (unitTemp == "imperial") {
                                   temp.text = kelvinToFahrenheit(today.main.temp)?.toInt().toString()
                                   feelsLike.text = kelvinToFahrenheit(main.feels_like).toString()
                               }else if (unitTemp == "metric") {
                                   temp.text = kelvinToCelsius(today.main.temp)?.toInt().toString()
                                   feelsLike.text = kelvinToCelsius(main.feels_like).toString()
                               }else{
                                   temp.text = today.getTemperatureInInt().toString()
                                   feelsLike.text = main.feels_like.toString()
                               }
                               var weather: Weather = today.weather.get(0)
                               val formattedDate = formatDate(room_data.data.list.get(0).dt)
                             if(language == "en"){
                                 desc.text = weather.description
                                 date.text = formattedDate
                             }else if (language == "ar") {
                                 translateCityName(weather.description) { translatedCityName ->
                                     desc.text = translatedCityName
                                     Log.i(TAG, "onViewCreated: the returned city when Ar : $translatedCityName")
                                 }
                                 translateCityName(formattedDate) { translatedCityName ->
                                     date.text = translatedCityName
                                     Log.i(TAG, "onViewCreated: the returned city when Ar : $translatedCityName")
                                 }

                             }else {
                                 desc.text = weather.description
                                 date.text = formattedDate
                             }


                               var wind: WindWeather = today.wind
                               Log.i("wind", "onViewCreated:${wind} ")
                               if (windSpeed == "Meter/Sec") {
                                   var formattedWind = formatWindInMeterPerSec(wind)
                                   windTV.text = formattedWind + "m/s"
                               } else if (windSpeed == "Mile/Hour") {
                                   var formattedWind = formatWindInMilesPerHour(wind)
                                   windTV.text = formattedWind + "mph"
                               }else{
                                   var formattedWind = formatWindInMeterPerSec(wind)
                                   windTV.text = formattedWind + "m/s"
                               }
                               var clouds: Clouds = today.clouds
                               var formatedClouds = formatCloudiness(clouds)
                               cloudTV.text = formatedClouds
                               var formattedHumidity = formatHumidity(main)
                               humidityTV.text = formattedHumidity
                               var formattedPressure = formatPressure(main)
                               presstureTV.text = formattedPressure
                               var UV = today.sys.pod
                               UVTV.text = UV
                               visibTV.text = today.visibility.toString() + " m"
                               val url_img = "https://openweathermap.org/img/wn/" + weather.icon + ".png"
                               Glide.with(this@HomeFragment).load(url_img)
                                   .apply(
                                       RequestOptions()
                                       //  .placeholder(R.drawable.placeholder) // don't forget the placeholder image
                                       //  .error(R.drawable.placeholder)
                                   )
                                   .into(current_temp_img)
                               val filteredList = mutableListOf<WeatherItem>()
                               val hourlyList = mutableListOf<WeatherItem>()

                               var isFirstDay = true
                               var previousDate = ""
                               room_data.data.list.forEach { weatherData ->
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
                               adapter.submitList(filteredList)
                               Log.i(
                                   TAG,
                                   "onViewCreated: The Weather Forecast For 5 days left is: $filteredList"
                               )

                           val firstDayDate = getDatePart(room_data.data.list.get(0).dt_txt)
                           room_data.data.list.forEach {
                               weatherItem ->
                               if(weatherItem.dt_txt.contains(firstDayDate))
                                   hourlyList.add(weatherItem)
                                   houradapter.submitList(hourlyList)
                           }
                           Log.i(TAG, "onViewCreated: FFFFFFINALLY  : ${hourlyList}  , firstDay = $firstDayDate")
                       }is WeatherForeCastState.Failure -> {
                               weekly_forecast.visibility = View.GONE
                               progressBar.visibility = View.GONE
                               Toast.makeText(
                                   requireContext(),
                                   "Error loading data!",
                                   Toast.LENGTH_SHORT
                               ).show()
                          }

                           else -> {}
                       }
               }
           }
       }
        lifecycleScope.launch {
            settingViewModel.settingsFlow.collectLatest { setting ->
                unitTemp = setting.unitsTemp
                language = setting.language
                locationSett = setting.location
                windSpeed = setting.windSpeed
                val layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
                val layoutManagerHourly = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
                adapter = WeeklyForecastListAdapter(requireContext(),setting)
                houradapter= HourlyWeatherListAdapter(requireContext(),setting)
                weekly_forecast.adapter = adapter
                houlry_forecast.adapter = houradapter
                weekly_forecast.layoutManager = layoutManager
                houlry_forecast.layoutManager = layoutManagerHourly
                lifecycleScope.launch(Dispatchers.Main) {
                    when (language) {
                        "ar" -> {
                            // If the language is English, translate the city name to Arabic
                            translateCityName(country.countryName) { translatedCityName ->
                                // Update the location text view with the translated city name
                               // yourLocation.text = translatedCityName
                                //    yourLocation.invalidate()
                               // Log.i(TAG, "onViewCreated: the returned city when Ar : $translatedCityName")
                            }
                        }
                        "en" -> {
                         //   yourLocation.text = country.countryName
                            yourLocation.invalidate()
                          //  Log.i(TAG, "onViewCreated: the returned city when en : ${country.countryName}")
                        }
                    }
                }
                Log.i(
                    TAG,
                    "collectSettingsData: 1-$unitTemp , 2-$windSpeed , 3-$language , 4-$locationSett "
                )   //Ask Heba
            }
        }

    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: ")
        settingViewModel.emitChangingSetting()
        lifecycleScope.launch {
            rowCount = weatherViewModel.returnRowCount()
            Log.i(TAG, "onStart: rowCounts = $rowCount")
           /* if (rowCount != 0) {
                Log.i(TAG, "onStart: inside condition rowCounts")
                weatherViewModel.getHomeWeatherFromRoom()
            }*/
        }
    }

    override fun onResume() {
        Log.i(TAG, "onStart: ")
        super.onResume()
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
    fun kelvinToCelsius(kelvin: Double): Double? {
        return (kelvin - 273.15)
    }

    fun kelvinToFahrenheit(kelvin: Double): Double? {
        return (kelvin * 9/5 - 459.67)
    }
    fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp * 1000 // Convert seconds to milliseconds
        }
        val dateFormat = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTimeFormatted(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDateTime.format(formatter)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFreshLocation()
            }
        }

    }

    private fun checkPermissions(): Boolean {
        return (checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        Log.i(TAG, "getFreshLocation: inside this function")
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            },
            object : LocationCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation
                    Log.i(TAG, "getFreshLocation: inside first condition function $location")
                    if (location != null) {
                        Log.i(TAG, "getFreshLocation: inside first condition function")
                        todayDate = getCurrentDateTimeFormatted()
                        Log.i(TAG, "onViewCreated: Current Date is $todayDate")
                        longitude = location.longitude
                        latitude  = location.latitude

                        lifecycleScope.launch{
                            countryFromApi = getStringFromSharedPreferences()
                            rowCount = weatherViewModel.returnRowCount()
                            Log.i(TAG, "getFreshLocation: The Rows Number : $rowCount and countryFromSharedApi $countryFromApi")
                           // weatherViewModel.getHomeWeatherFromRoom()
                                   if(locationSett == "gps"){
                                       weatherViewModel.fetchDataofWeatherForeCast(latitude,longitude, API_KEY ,"standard" , "en",todayDate)
                                   }else if (locationSett == "map"){
                                       weatherViewModel.fetchDataofWeatherForeCast(latitudeFromSett,longitudeFromSett, API_KEY ,"standard" , "en",todayDate)
                                   }
                        }
                        Log.i(TAG, "onLocationResult: TESTTTTTTTTTT WHICH FIRST 1")
                        Log.i(TAG, "onLocationResult: <<<<<<<<<<<<<<<<<<<<<<<<<<The Unit Temp >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>: $unitTemp and longFromSett $longitudeFromSett")
                    }
                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun enableLocationServices() {
        Toast.makeText(requireContext(), "Turn On Location", Toast.LENGTH_LONG).show();
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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

    private fun saveStringToSharedPreferences(value: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("country_api",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(PREF_KEY, value)
        editor.apply()
    }

    private fun getStringFromSharedPreferences(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("country_api",Context.MODE_PRIVATE)
        return sharedPreferences.getString(PREF_KEY, "") ?: ""
    }

}