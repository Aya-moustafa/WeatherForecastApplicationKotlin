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
import com.example.weatherforecastapplicationkotlin.favorites.viewmodel.FavoritesViewModel
import com.example.weatherforecastapplicationkotlin.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() ,OnDeleteFavClickListener {
    private final var TAG :String = "FavoritesFragment"
    lateinit var mapBtnToAdd : FloatingActionButton
    lateinit var recycleView : RecyclerView
    lateinit var adapter: FavoritePlacesListAdapter
    lateinit var viewModel : FavoritesViewModel
    lateinit  var favFactory : FavoritesViewModelFactory
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
            TodayWeatherLocalDataSource(requireContext())
        ))

        viewModel = ViewModelProvider(this,favFactory).get(FavoritesViewModel::class.java)
        adapter = FavoritePlacesListAdapter(requireContext()){
            country -> this.onClicktodelete(country)
        }
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

       /* requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to HomeFragment
          //      backToHomeFragment()
            }
        })*/
    }

    override fun onClicktodelete(country: Country) {
        viewModel.deletePlace(country)
    }

    /*private fun backToHomeFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .addToBackStack(null)
            .commit()
    }*/

}