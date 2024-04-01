package com.example.weatherforecastapplicationkotlin.setting.model

import java.io.Serializable

data class SettingOptions(
    var unitsTemp : String = "standard",
    var windSpeed : String = "Meter/Sec",
    var language  : String = "en",
    var location  : String = "gpsc"
    ) : Serializable