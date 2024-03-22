package com.example.weatherforecastapplicationkotlin.setting.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewMode
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewModelFactory
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.database.WeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.home_page.view.HomeFragment
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModelFactory
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.setting.model.SettingOptions

class SettingFragment : Fragment() {
    private final var TAG :String = "SettingFrag"

    lateinit var celSwitch : SwitchCompat
    lateinit var kelSwitch : SwitchCompat
    lateinit var fehrSwitch : SwitchCompat
    lateinit var engSwitch  : SwitchCompat
    lateinit var arabSwitch : SwitchCompat
    lateinit var gpsSwitch  : SwitchCompat
    lateinit var mapSwitch  : SwitchCompat
    lateinit var wind_mile_hour : SwitchCompat
    lateinit var wind_meter_sec : SwitchCompat
    lateinit var enable_notification : SwitchCompat
    lateinit var  allSettings : SettingOptions
    lateinit  var settingViewModel: SettingViewMode
    lateinit var  sharedFactory: SettingViewModelFactory
    lateinit var backBtn : ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        allSettings  = settingViewModel.getSavedSettings()
        displayUpdatedSettings(allSettings)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        celSwitch = view.findViewById(R.id.switchcels)
        kelSwitch= view.findViewById(R.id.switchkel)
        fehrSwitch= view.findViewById(R.id.switchfehr)
        backBtn   = view.findViewById(R.id.backBtn)
        engSwitch = view.findViewById(R.id.switcheng)
        arabSwitch= view.findViewById(R.id.switcharb)
        wind_meter_sec=  view.findViewById(R.id.windmeter_sec_switch)
        wind_mile_hour = view.findViewById(R.id.windmile_hour_swtich)
        gpsSwitch = view.findViewById(R.id.switchgps)
        mapSwitch = view.findViewById(R.id.switchmap)
        enable_notification = view.findViewById(R.id.switchnotif)
        sharedFactory = SettingViewModelFactory(requireActivity().application)
        settingViewModel = ViewModelProvider(this,sharedFactory).get(SettingViewMode::class.java)
        allSettings = settingViewModel.getSavedSettings()
        settingViewModel.updateSettings(allSettings)
        displayUpdatedSettings(allSettings)
        Log.i(TAG, "onViewCreatedd: The Settings is $allSettings")
        celSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                allSettings.unitsTemp = "metric"
                settingViewModel.updateSettings(allSettings)
                kelSwitch.isChecked = false
                fehrSwitch.isChecked = false
            }
        }

        // Set listener for Arabic switch
        kelSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                allSettings.unitsTemp = "standard"
                settingViewModel.updateSettings(allSettings)
                celSwitch.isChecked = false
                fehrSwitch.isChecked = false
            }
        }

        fehrSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                allSettings.unitsTemp = "imperial"
                settingViewModel.updateSettings(allSettings)
                celSwitch.isChecked = false
                kelSwitch.isChecked = false
            }
        }


        engSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                allSettings.language = "en"
                settingViewModel.updateSettings(allSettings)
                arabSwitch.isChecked = false
            }
        }

        arabSwitch.setOnCheckedChangeListener { buttonView , isChecked ->
            if(isChecked){
                allSettings.language = "ar"
                settingViewModel.updateSettings(allSettings)
                engSwitch.isChecked = false
            }
        }

        gpsSwitch.setOnCheckedChangeListener { buttonView , isChecked ->
            if(isChecked){
                allSettings.location = "gps"
                settingViewModel.updateSettings(allSettings)
                mapSwitch.isChecked = false
            }
        }

        mapSwitch.setOnCheckedChangeListener { buttonView , isChecked ->
            if(isChecked){
                allSettings.location = "map"
                settingViewModel.updateSettings(allSettings)
                gpsSwitch.isChecked = false
            }
        }

        wind_meter_sec.setOnCheckedChangeListener { buttonView , isChecked ->
            if(isChecked){
                allSettings.windSpeed = "Meter/Sec"
                settingViewModel.updateSettings(allSettings)
                wind_mile_hour.isChecked = false
            }
        }

        wind_mile_hour.setOnCheckedChangeListener { buttonView , isChecked ->
            if(isChecked){
                allSettings.windSpeed = "Mile/Hour"
                settingViewModel.updateSettings(allSettings)
                wind_meter_sec.isChecked = false
            }
        }

    }

    private fun getDefaultSettings() {
        kelSwitch.isChecked = true
        engSwitch.isChecked = true
        gpsSwitch.isChecked = true
        wind_meter_sec.isChecked = true
    }

    private fun displayUpdatedSettings(allSettings: SettingOptions) {
        val temp = allSettings.unitsTemp
        val location = allSettings.location
        val language = allSettings.language
        val wind = allSettings.windSpeed

        Log.i(
            TAG,
            "displayUpdatedSettings: tempUnit = $temp , location = $location , language = $language , windSpeed = $wind"
        )


        if (allSettings.unitsTemp == "metric") {
            celSwitch.isChecked = true
            fehrSwitch.isChecked = false
            kelSwitch.isChecked = false
        } else if (allSettings.unitsTemp == "standard") {
            celSwitch.isChecked = false
            fehrSwitch.isChecked = false
            kelSwitch.isChecked = true
        } else if (allSettings.unitsTemp == "imperial") {
            celSwitch.isChecked = false
            fehrSwitch.isChecked = true
            kelSwitch.isChecked = false
        }

        if (allSettings.language == "en") {
            engSwitch.isChecked = true
            arabSwitch.isChecked = false
        } else if (allSettings.language == "ar") {
            engSwitch.isChecked = false
            arabSwitch.isChecked = true
        }

        if (allSettings.location == "gps") {
            gpsSwitch.isChecked = true
            mapSwitch.isChecked = false
        } else if (allSettings.location == "map") {
            gpsSwitch.isChecked = false
            mapSwitch.isChecked = true
        }

        if (allSettings.windSpeed == "Meter/Sec") {
            wind_meter_sec.isChecked = true
            wind_mile_hour.isChecked = false
        } else if (allSettings.windSpeed == "Mile/Hour") {
            wind_meter_sec.isChecked = false
            wind_mile_hour.isChecked = true
        }


    }
}