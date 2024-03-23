package com.example.weatherforecastapplicationkotlin.home_page.view


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecastapplicationkotlin.MainActivity.API_KEY
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewMode
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewModelFactory
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.database.WeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModelFactory
import com.example.weatherforecastapplicationkotlin.model.Clouds
import com.example.weatherforecastapplicationkotlin.model.Main
import com.example.weatherforecastapplicationkotlin.model.Weather
import com.example.weatherforecastapplicationkotlin.model.WeatherItem
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.model.Wind
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.setting.model.SettingOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val REQUEST_LOCATION_CODE = 2005

class HomeFragment : Fragment() {
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
    private lateinit var current_temp_img : ImageView
    lateinit var  adapter : WeeklyForecastListAdapter
   // private lateinit var weekly_forecast_List : List<WeatherItem>



    private final var TAG :String = "HomeFrag"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_CODE = 1001 // Your request code
    private  var longitude: Double = 0.0
    private  var latitude: Double  = 0.0
    lateinit  var weatherViewModel: WeatherViewModel
    lateinit var  weatherFactory: WeatherViewModelFactory
    lateinit  var settingViewModel: SettingViewMode
    lateinit var  sharedFactory: SettingViewModelFactory
     var  unitTemp  : String = "standard"
     var  windSpeed : String = "Meter/Sec"
     var  language  : String = "en"
     var  location  : String = "gps"



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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        current_temp_img = view.findViewById(R.id.currentTempImg)
        yourLocation = view.findViewById(R.id.location)
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

        if (isLocationEnabled()) {
            if (checkPermissions()) {
                getFreshLocation()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
            }
        } else {
            enableLocationServices()
        }
        weatherFactory = WeatherViewModelFactory(WeatherRepository.getInstance(WeatherRemoteDataSource.getInstance(),
            WeatherLocalDataSource(requireContext())
        ),requireContext())
        weatherViewModel = ViewModelProvider(this,weatherFactory).get(WeatherViewModel::class.java)
        sharedFactory = SettingViewModelFactory(requireActivity().application)
        settingViewModel = ViewModelProvider(this,sharedFactory).get(SettingViewMode::class.java)
        val layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        adapter = WeeklyForecastListAdapter(requireContext())
        weekly_forecast.adapter = adapter
        weekly_forecast.layoutManager = layoutManager
        weatherViewModel.weather.observe(viewLifecycleOwner){
            current_weather ->
            yourLocation.text= current_weather.name
            var main : Main = current_weather.main
            temp.text = current_weather.getTemperatureInInt().toString()
            //   temp.text        = current_weather.getTemperatureInCelsius().toString()

            feelsLike.text   = current_weather.getTempFeelsLikeInInt().toString()
            var weather : Weather = current_weather.weather.get(0)
            desc.text        = weather.description
            val formattedDate = formatDate(current_weather.dt)
            date.text        = formattedDate
            var wind : Wind = current_weather.wind
            var formattedWind = formatWind(wind)
            windTV.text      = formattedWind
            var clouds : Clouds = current_weather.clouds
            var formatedClouds = formatCloudiness(clouds)
            cloudTV.text = formatedClouds
            var formattedHumidity = formatHumidity(main)
            humidityTV.text   = formattedHumidity
            var formattedPressure = formatPressure(main)
            presstureTV.text  = formattedPressure
            var UV  = current_weather.getUV()
            UVTV.text = UV.toString()
            visibTV.text = current_weather.visibility.toString()+" m"
            val url_img = "https://openweathermap.org/img/wn/" + weather.icon + ".png"
            Glide.with(this@HomeFragment).load(url_img)
                .apply(
                    RequestOptions()
                    //  .placeholder(R.drawable.placeholder) // don't forget the placeholder image
                    //  .error(R.drawable.placeholder)
                )
                .into(current_temp_img)
            Log.i(TAG, "onViewCreated:  The WEAAA = $current_weather ")
        }

        weatherViewModel.weatherForecast.observe(viewLifecycleOwner) { weatherForecast ->
            val filteredList = mutableListOf<WeatherItem>()

            var isFirstDay = true
            var previousDate = ""
            weatherForecast.list.forEach { weatherData ->
                val currentDate = weatherData.dt_txt.substring(0, 10) // Extracting date without time
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
            Log.i(TAG, "onViewCreated: The Weather Forecast For 5 days left is: $filteredList")
        }
        lifecycleScope.launch {
             weatherViewModel.settingsFlow.collectLatest{
                 setting ->
                 unitTemp = setting.unitsTemp
                 language = setting.language
                 location = setting.location
                 windSpeed= setting.windSpeed
                 Log.i(TAG, "collectSettingsData: 1-$unitTemp , 2-$windSpeed , 3-$language , 4-$location ")   //Ask Heba
            }
        }
    }


    override fun onStart() {
        super.onStart()
        weatherViewModel.emitChangingSetting()

    }
    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause: ")
    }

    
    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView: ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i(TAG, "onDetach: ")
    }

    override fun onResume() {
        super.onResume()

    }

    fun formatPressure(main : Main): String {
        return "${main.pressure} hPa"
    }

    fun formatHumidity(main: Main): String {
        return "${main.humidity}%"
    }
    fun formatCloudiness(clouds: Clouds): String {
        return "${clouds.all}%"
    }
    fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp * 1000 // Convert seconds to milliseconds
        }
        val dateFormat = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun formatWind(wind: Wind): String {
        return "${String.format("%.2f", wind.speed)} m/s"
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            },
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation

                    if (location != null) {
                        longitude = location.longitude
                        latitude  = location.latitude
                        weatherViewModel.getWeather(latitude, longitude, API_KEY,unitTemp)
                        weatherViewModel.getWeatherForecast(latitude,longitude, API_KEY,unitTemp)
                        Log.i(TAG, "onLocationResult: <<<<<<<<<<<<<<<<<<<<<<<<<<The Unit Temp >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>: $unitTemp")
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

    fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, requireContext().resources.configuration.locale)
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

        return if (addresses != null && addresses.isNotEmpty()) {
            val address: Address = addresses[0]
            val sb = StringBuilder()
            for (i in 0..address.maxAddressLineIndex) {
                sb.append(address.getAddressLine(i)).append("\n")
            }
            sb.toString()
        } else {
            "No address found"
        }
    }

}