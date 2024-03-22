package com.example.weatherforecastapplicationkotlin.setting.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.setting.model.SettingOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


//This ViewModel Shared Between Fragments
class SettingViewMode(private val application: Application) : ViewModel() {
    private var settingsSharedPreference = application.getSharedPreferences("Setting",Context.MODE_PRIVATE)
    private val _settings: MutableSharedFlow<SettingOptions> = MutableSharedFlow<SettingOptions>(replay = 1)
    val settings: SharedFlow<SettingOptions> = _settings

    fun updateSettings(settingOptions: SettingOptions) {

            with(settingsSharedPreference.edit()){
                putString("selectedTempertureUnit" , settingOptions.unitsTemp)
                putString("selectedWindSpeed",settingOptions.windSpeed)
                putString("selectedLocation",settingOptions.location)
                putString("selectedLanguage",settingOptions.language)
                apply()
            }
    }

    fun getSavedSettings() : SettingOptions {
        return SettingOptions(
            settingsSharedPreference.getString("selectedTempertureUnit","") ?: "",
            settingsSharedPreference.getString("selectedWindSpeed","") ?: "",
            settingsSharedPreference.getString("selectedLocation","") ?: "",
            settingsSharedPreference.getString("selectedLanguage","") ?: ""
        )
    }

}