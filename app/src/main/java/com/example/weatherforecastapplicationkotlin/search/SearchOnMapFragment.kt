package com.example.weatherforecastapplicationkotlin.search

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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherforecastapplicationkotlin.MainActivity.isSearchOnMapOpenedFromSetting
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.database.WeatherLocalDataSource
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.weatherforecastapplicationkotlin.favorites.view.FavoritesFragment
import com.example.weatherforecastapplicationkotlin.favorites.viewmodel.FavoritesViewModel
import com.example.weatherforecastapplicationkotlin.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class SearchOnMapFragment : Fragment() , OnMapReadyCallback {
    private final var TAG : String = "SearchOnMapFragment"
    lateinit var gMap : GoogleMap
    lateinit var map :FrameLayout
    lateinit  var favFactory : FavoritesViewModelFactory
    lateinit var  fav_view_model: FavoritesViewModel
    var  fromSetting : Boolean = false
    var  fromFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_on_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map = view.findViewById(R.id.map)
        if (arguments?.getBoolean("fromSetting") == true) {
            // Fragment opened from SettingFragment, handle accordingly
            isSearchOnMapOpenedFromSetting = true
        }
        fromSetting = arguments?.getBoolean("fromSetting") ?: false
        favFactory = FavoritesViewModelFactory(WeatherRepository.getInstance(WeatherRemoteDataSource.getInstance(), WeatherLocalDataSource(requireContext())))
        fav_view_model = ViewModelProvider(this,favFactory).get(FavoritesViewModel::class.java)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to HomeFragment
                backToHomeFragment()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (isSearchOnMapOpenedFromSetting) {
            // Clear the flag to prevent reopening the fragment again
            isSearchOnMapOpenedFromSetting = false
        }
    }

    private fun backToHomeFragment() {
        /*requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FavoritesFragment())
            .addToBackStack(null)
            .commit()*/
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
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    val cityName = getCityName(addresses)
                    if (!cityName.isNullOrEmpty()) {
                        val country = Country(cityName, latLng.latitude, latLng.longitude)
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
                }
                snackbar.show()

            }
        } else if(fromSetting) {
            gMap.setOnMapClickListener { latLng ->
                addMarker(latLng)

                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val cityName = getCityName(addresses)
                if (!cityName.isNullOrEmpty()) {
                    AlertDialog.Builder(requireContext())
                        .setMessage("$cityName")
                        .setPositiveButton("Save") { dialog, _ ->
                            val navController = findNavController()
                            navController.navigate(R.id.homeFragment2)

                            dialog.dismiss()
                        }
                        .setNegativeButton("Dismiss") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
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

}