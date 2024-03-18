package com.example.weatherforecastapplicationkotlin.setting.view

import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapplicationkotlin.MainActivity.viewmodel.SharedViewMode
import com.example.weatherforecastapplicationkotlin.MainActivity.viewmodel.SharedViewModelFactory
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.home_page.view.HomeFragment
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModelFactory
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.setting.model.SettingOptions
import kotlin.math.log

class SettingFragment : Fragment() {
    private final var TAG :String = "SettingFrag"

    lateinit var celSwitch : SwitchCompat
    lateinit var kelSwitch : SwitchCompat
    lateinit var fehrSwitch : SwitchCompat
    var unitsTemp: String? = "standard"
    lateinit var  allSettings : SettingOptions
    lateinit  var sharedViewModel: SharedViewMode
    lateinit var  sharedFactory: SharedViewModelFactory
    lateinit var backBtn : ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        sharedFactory = SharedViewModelFactory()
        sharedViewModel = ViewModelProvider(requireActivity(),sharedFactory).get(SharedViewMode::class.java)

        if (unitsTemp == "metric") {
            celSwitch.isChecked = true
            kelSwitch.isChecked = false
            fehrSwitch.isChecked = false

        } else if (unitsTemp == "standard") {
            celSwitch.isChecked = false
            fehrSwitch.isChecked = false
            kelSwitch.isChecked = true
        }else if (unitsTemp == "imperial") {
            celSwitch.isChecked = false
            fehrSwitch.isChecked = true
            kelSwitch.isChecked = false
        }else{
             unitsTemp = "standard"
            celSwitch.isChecked = false
            fehrSwitch.isChecked = false
            kelSwitch.isChecked = false
        }

        celSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                unitsTemp = "metric"
                kelSwitch.isChecked = false
                fehrSwitch.isChecked = false
                allSettings = SettingOptions(unitsTemp!!)
                sharedViewModel.updateSettings(allSettings)
            } else {
                unitsTemp = "standard"
            }
        }

        // Set listener for Arabic switch
        kelSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                unitsTemp = "standard"
                celSwitch.isChecked = false
                fehrSwitch.isChecked = false
                allSettings = SettingOptions(unitsTemp!!)
                sharedViewModel.updateSettings(allSettings)
            } else {
                unitsTemp = "standard"
            }
        }

        fehrSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                unitsTemp = "imperial"
                celSwitch.isChecked = false
                kelSwitch.isChecked = false
                allSettings = SettingOptions(unitsTemp!!)
                sharedViewModel.updateSettings(allSettings)
            } else {
                unitsTemp = "standard"
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to HomeFragment
                backToHomeFragment()
            }
        })
        backBtn.setOnClickListener {
            backToHomeFragment()
        }
    }


    private fun backToHomeFragment () {
        Log.i(TAG, "onViewCreated:<<<<<<<<<<<<<<<<The Updated Setting is ${allSettings.unitsTemp}>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .addToBackStack(null)
            .commit()
    }
}