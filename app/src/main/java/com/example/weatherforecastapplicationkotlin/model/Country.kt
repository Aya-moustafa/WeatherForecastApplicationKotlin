package com.example.weatherforecastapplicationkotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "Fav_Places")

data class Country(
    @NotNull
    @PrimaryKey
    var countryName : String,
    @NotNull
    var latitude : Double,
    @NotNull
    var longtuide : Double
) : Serializable