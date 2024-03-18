package com.example.weatherforecastapplicationkotlin.MainActivity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplicationkotlin.setting.model.SettingOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


//This ViewModel Shared Between Fragments
class SharedViewMode : ViewModel() {
    private val _settings: MutableSharedFlow<SettingOptions> = MutableSharedFlow<SettingOptions>()
    val settings: SharedFlow<SettingOptions> = _settings.asSharedFlow()

    fun updateSettings(settingOptions: SettingOptions) {
        viewModelScope.launch {
            _settings.emit(settingOptions)
        }
    }
}