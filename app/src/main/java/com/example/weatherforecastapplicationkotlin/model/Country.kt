package com.example.weatherforecastapplicationkotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "Fav_Places")

data class Country(
    var countryName : String,
    @PrimaryKey
    @NotNull
    var cityName  : String,
    @NotNull
    var latitude : Double,
    @NotNull
    var longtuide : Double
) : Serializable