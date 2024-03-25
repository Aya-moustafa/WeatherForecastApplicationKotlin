package com.example.weatherforecastapplicationkotlin.setting.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplicationkotlin.MainActivity.isMapSwitchChecked
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.setting.model.SettingOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


//This ViewModel Shared Between Fragments
class SettingViewMode(private val application: Application) : ViewModel() {
    private var settingsSharedPreference = application.getSharedPreferences("Setting",Context.MODE_PRIVATE)
    private val _settingsFlow = MutableSharedFlow<SettingOptions>(replay = 1)
    val settingsFlow: SharedFlow<SettingOptions> = _settingsFlow
    private var mapFragmentOpenedFromSetting = false

    fun updateSettings(settingOptions: SettingOptions) {

            with(settingsSharedPreference.edit()){
                putString("selectedTempertureUnit" , settingOptions.unitsTemp)
                putString("selectedWindSpeed",settingOptions.windSpeed)
                putString("selectedLocation",settingOptions.location)
                putString("selectedLanguage",settingOptions.language)
                putBoolean("isMapSwitchChecked",isMapSwitchChecked )
                apply()
            }
    }
    fun emitChangingSetting () {
        val updatedSetting = getSavedSettings()
        Log.i("Init", "inInit Scope: $updatedSetting ")
        viewModelScope.launch {
            _settingsFlow.emit(updatedSetting)
        }
    }
    fun getSavedSettings() : SettingOptions {
        return SettingOptions(
            settingsSharedPreference.getString("selectedTempertureUnit","") ?: "",
            settingsSharedPreference.getString("selectedWindSpeed","") ?: "Meter/Sec",
            settingsSharedPreference.getString("selectedLanguage","") ?: "",
            settingsSharedPreference.getString("selectedLocation","") ?: ""
        )
    }

    fun getMapState () : Boolean {
        return settingsSharedPreference.getBoolean("isMapSwitchChecked",false)
    }
    fun isMapFragmentOpenedFromSetting() = mapFragmentOpenedFromSetting

    fun setMapFragmentOpenedFromSetting(opened: Boolean) {
        mapFragmentOpenedFromSetting = opened
    }

}