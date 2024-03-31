package com.example.weatherforecastapplicationkotlin.favorites.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.WeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_for_home_page.TodayWeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_of_notification.NotificationDeatilsLocalDataSource
import com.example.weatherforecastapplicationkotlin.favorites.viewmodel.FavoritesViewModel
import com.example.weatherforecastapplicationkotlin.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weatherforecastapplicationkotlin.home_page.view.WeeklyForecastListAdapter
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.setting.model.SettingOptions
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewMode
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() ,OnDeleteFavClickListener {
    private final var TAG :String = "FavoritesFragment"
    lateinit var mapBtnToAdd : FloatingActionButton
    lateinit var recycleView : RecyclerView
    lateinit var adapter: FavoritePlacesListAdapter
    lateinit var viewModel : FavoritesViewModel
    lateinit  var favFactory : FavoritesViewModelFactory
    lateinit  var settingViewModel: SettingViewMode
    lateinit var  sharedFactory: SettingViewModelFactory
    var  unitTemp  : String = "standard"
    var  windSpeed : String = "Meter/Sec"
    var  language  : String = "en"
    var  locationSett  : String = "gps"
    lateinit var settingNeed : SettingOptions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapBtnToAdd = view.findViewById(R.id.fab_add)
        recycleView = view.findViewById(R.id.fav_places_recycle)
        favFactory = FavoritesViewModelFactory(WeatherRepository.getInstance(WeatherRemoteDataSource.getInstance(), WeatherLocalDataSource(requireContext()),
            TodayWeatherLocalDataSource(requireContext()),
            NotificationDeatilsLocalDataSource(requireContext())
        ))

        viewModel = ViewModelProvider(this,favFactory).get(FavoritesViewModel::class.java)
        sharedFactory = SettingViewModelFactory(requireActivity().application)
        settingViewModel = ViewModelProvider(this,sharedFactory).get(SettingViewMode::class.java)
        adapter = FavoritePlacesListAdapter(requireContext(),
            { country -> this.onClicktodelete(country) },
            { country -> this.onItemClick(country) }
        )
        val layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        recycleView.adapter = adapter
        recycleView.layoutManager = layoutManager

       viewModel.getLocalPlaces()
        lifecycleScope.launch {
            viewModel.favplaces.collect{
                    places ->
                adapter.submitList(places)
                Log.i(TAG, "onViewCreated: Favorite Products : $places")

            }
        }

        mapBtnToAdd.setOnClickListener {
            val navController = findNavController()
            val bundle = Bundle().apply {
                putBoolean("fromFavorite", true) // Pass your boolean flag here
            }
            navController.navigate(R.id.searchOnMapFragment, bundle)
        }

        lifecycleScope.launch {
            settingViewModel.settingsFlow.collectLatest { setting ->
                unitTemp = setting.unitsTemp
                language = setting.language
                locationSett = setting.location
                windSpeed = setting.windSpeed
                settingNeed = setting
                val layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
                Log.i(
                    TAG,
                    "collectSettingsData: 1-$unitTemp , 2-$windSpeed , 3-$language , 4-$locationSett "
                )
            }
        }
    }

    override fun onClicktodelete(country: Country) {
        viewModel.deletePlace(country)
    }

    override fun onItemClick(country: Country) {
        val navController = findNavController()
        val bundle = Bundle().apply {
            putSerializable("fav_country", country) // Pass your boolean flag here
            putSerializable("setting_data",settingNeed)
        }
        navController.navigate(R.id.favoriteDetailsFragment, bundle)
    }


}