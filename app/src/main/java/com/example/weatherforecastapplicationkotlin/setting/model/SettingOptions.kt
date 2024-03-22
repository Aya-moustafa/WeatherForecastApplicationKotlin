package com.example.weatherforecastapplicationkotlin.setting.model

import java.io.Serializable

data class SettingOptions(
    var unitsTemp : String,
    var windSpeed : String,
    var language  : String,
    var location  : String
    )