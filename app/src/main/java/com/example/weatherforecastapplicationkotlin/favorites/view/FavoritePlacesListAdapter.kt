package com.example.weatherforecastapplicationkotlin.favorites.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.model.Country

class FavoritePlacesListAdapter(private var context: Context) : ListAdapter<Country, FavoritePlacesListAdapter.FavWeatherViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavWeatherViewHolder {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.fav_places_item, parent, false)
        return FavWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavWeatherViewHolder, position: Int) {
        var country : Country = getItem(position)
        holder.placeName.text = country.countryName
    }

    inner class FavWeatherViewHolder(private val layout: View) : RecyclerView.ViewHolder(layout) {
         val placeName: TextView = layout.findViewById(R.id.place_name)
         val placeCard : CardView= layout.findViewById(R.id.place_card)
    }

    companion object {
        private const val TAG = "UsersAdapter"
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<Country>() {
    override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
        return oldItem == newItem
    }
}