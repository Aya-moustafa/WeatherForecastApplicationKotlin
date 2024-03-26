package com.example.weatherforecastapplicationkotlin.search_feature

import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecastapplicationkotlin.MainActivity.isSearchOnMapOpenedFromSetting
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.WeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.favorites.viewmodel.FavoritesViewModel
import com.example.weatherforecastapplicationkotlin.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewMode
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import androidx.navigation.fragment.findNavController
import com.example.weatherforecastapplicationkotlin.database.data_for_home_page.TodayWeatherLocalDataSource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class LocationMapFragment : Fragment() , OnMapReadyCallback {
    private final var TAG : String = "LocationMapFragment"
    private lateinit var translateArabic : Translator
    lateinit var gMap : GoogleMap
    lateinit var map : FrameLayout
    lateinit  var favFactory : FavoritesViewModelFactory
    lateinit var  fav_view_model: FavoritesViewModel
    var  fromSetting : Boolean = false
    var  fromFavorite: Boolean = false
    lateinit  var settingViewModel: SettingViewMode
    lateinit var  sharedFactory: SettingViewModelFactory
    var language : String = "en"
    var cityInArabiclan : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map = view.findViewById(R.id.map)
        sharedFactory = SettingViewModelFactory(requireActivity().application)
        settingViewModel = ViewModelProvider(this,sharedFactory).get(SettingViewMode::class.java)
        if (arguments?.getBoolean("fromSetting") == true) {
            // Fragment opened from SettingFragment, handle accordingly
            isSearchOnMapOpenedFromSetting = true
        }
        fromSetting = arguments?.getBoolean("fromSetting") ?: false
        fromFavorite = arguments?.getBoolean("fromFavorite") ?: false

        favFactory = FavoritesViewModelFactory(WeatherRepository.getInstance(WeatherRemoteDataSource.getInstance(), WeatherLocalDataSource(requireContext()),
            TodayWeatherLocalDataSource(requireContext())
        ))
        fav_view_model = ViewModelProvider(this,favFactory).get(FavoritesViewModel::class.java)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        lifecycleScope.launch {
            settingViewModel.settingsFlow.collectLatest {
                    setting ->
                language = setting.language
                Log.i(TAG, "onViewCreated: The Language is : $language")
            }
        }
        downloadModelToTranslate()
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

    override fun onResume() {
        super.onResume()
        if (isSearchOnMapOpenedFromSetting) {
            // Clear the flag to prevent reopening the fragment again
            isSearchOnMapOpenedFromSetting = false
        }
    }

    override fun onStart() {
        super.onStart()
        settingViewModel.emitChangingSetting()
    }



    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        if(fromFavorite) {
            gMap.setOnMapClickListener { latLng ->
                addMarker(latLng)
                val rootView = requireView()
                val snackbar = Snackbar.make(
                    rootView,
                    "Do you want to save this location ?",
                    Snackbar.LENGTH_LONG
                )
                    .setActionTextColor(Color.WHITE)
                    .setBackgroundTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.secondColor
                        )
                    )

                snackbar.setAction("Dismiss") {
                    snackbar.dismiss()
                }
                snackbar.setAction("Save") {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                        val addressesCountry = addresses?.get(0)?.countryName +" , "+addresses?.get(0)?.adminArea +" , "+ addresses?.get(0)?.featureName
                        if (addresses != null) {
                            Log.i(TAG, "onMapReady: The Address = ${addressesCountry}")
                        }
                        val cityName = getCityName(addresses)
                        if (!cityName.isNullOrEmpty() && addressesCountry != null) {
                            val country = Country(cityName,addressesCountry.toString(), latLng.latitude, latLng.longitude)
                            fav_view_model.insertProduct(country)
                            Toast.makeText(
                                requireContext(),
                                "insert ${cityName} Success..",
                                Toast.LENGTH_LONG
                            ).show()
                        } else
                            Toast.makeText(
                                requireContext(),
                                "Please select correct place",
                                Toast.LENGTH_LONG
                            ).show()
                        Log.i(TAG, "onMapReady: The City Name That Selected $cityName ")
                    }catch (e: IOException){
                        Log.e(TAG, "Error getting location information: ${e.message}")
                        Toast.makeText(requireContext(), "Check your connection and try again..", Toast.LENGTH_SHORT).show()
                    }
                }
                snackbar.show()

            }
        } else if (fromSetting) {
            gMap.setOnMapClickListener { latLng ->
                addMarker(latLng)

                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val cityName = getCityName(addresses)
                if (!cityName.isNullOrEmpty()) {
                    val country = Country(cityName,addresses.toString(), latLng.latitude, latLng.longitude)
                    alertDialogAppear(country)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please select correct place",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        fromSetting = false
        fromFavorite= false
    }

    private fun alertDialogAppear(country: Country) {
        translateCityName(country.countryName) { translatedCityName ->
            cityInArabiclan = translatedCityName
            Log.i(TAG, "onViewCreated: the returned city when ar : $translatedCityName")
            AlertDialog.Builder(requireContext())
                .setMessage("${cityInArabiclan}")
                .setPositiveButton("Save") { dialog, _ ->

                    val navController = findNavController()
                    val bundle = Bundle().apply {
                        putSerializable("country",country)
                        Log.i(TAG, "alertDialogAppear: ${country.countryName}")
                    }
                    navController.navigate(R.id.homeFragment,bundle)

                    dialog.dismiss()
                }
                .setNegativeButton("Dismiss") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun addMarker(laLng: LatLng) {
        gMap.clear()
        gMap.addMarker(MarkerOptions().position(laLng).title("Selected Location"))
        gMap.moveCamera(CameraUpdateFactory.newLatLng(laLng))
    }

    private fun getCityName(addresses: List<Address>?): String? {
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val cityName = address.locality
            if (!cityName.isNullOrBlank()) {
                return cityName
            }
        }
        return null
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

}