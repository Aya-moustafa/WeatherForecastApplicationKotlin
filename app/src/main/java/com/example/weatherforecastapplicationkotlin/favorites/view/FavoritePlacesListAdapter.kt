package com.example.weatherforecastapplicationkotlin.favorites.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.model.Country

class FavoritePlacesListAdapter(private var context: Context,private var deleteListener : (Country) -> Unit,private var clickItem : (Country) -> Unit) : ListAdapter<Country, FavoritePlacesListAdapter.FavWeatherViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavWeatherViewHolder {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.fav_places_item, parent, false)
        return FavWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavWeatherViewHolder, position: Int) {
        var country : Country = getItem(position)
        holder.country.text = country.countryName
        holder.placeName.text = country.cityName
        holder.delete.setOnClickListener{
            showDeleteConfirmationDialog(country)
        }
        holder.placeName.setOnClickListener {
            clickItem(country)
        }

    }


    private fun showDeleteConfirmationDialog(country: Country) {
        AlertDialog.Builder(context)
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete ${country.cityName}?")
            .setPositiveButton("Yes") { _, _ ->
                deleteListener(country)
            }
            .setNegativeButton("No", null)
            .show()
    }

    inner class FavWeatherViewHolder(private val layout: View) : RecyclerView.ViewHolder(layout) {
         val country: TextView = layout.findViewById(R.id.fromD_name)
        val placeName: TextView = layout.findViewById(R.id.fromTime_name)
        val delete : ImageView = layout.findViewById(R.id.delete)
        val placeCard : CardView= layout.findViewById(R.id.place_card_noti)
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