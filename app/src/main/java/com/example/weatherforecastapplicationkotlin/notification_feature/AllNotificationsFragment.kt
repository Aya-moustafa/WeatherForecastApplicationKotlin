package com.example.weatherforecastapplicationkotlin.notification_feature

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.WeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.database.data_for_home_page.TodayWeatherLocalDataSource
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModelFactory
import com.example.weatherforecastapplicationkotlin.model.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AllNotificationsFragment : Fragment() {
    private final var TAG : String  = "AllNotificationsFragment"
    private val KEY_PREF = "date_to_notification"
    private val KEY_PREF2 = "time_to_notification"
    lateinit var notifiRecycler : RecyclerView
    lateinit var notBtnToAdd : FloatingActionButton
    lateinit var dialog: AlertDialog
    lateinit var notificationData: NotificationData
    lateinit  var weatherViewModel: WeatherViewModel
    lateinit var  weatherFactory: WeatherViewModelFactory
    private var fromD : String = ""
    private var fromT : String = ""
    private var toD : String = ""
    private var toT : String = ""
    private var status : String = "notification"
    var weather_description : String = ""
    var date_in_room : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notifiRecycler= view.findViewById(R.id.notifications_recycle)
        notBtnToAdd= view.findViewById(R.id.fab_add_not)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.custom_dialog, null)
        var fromBtn : ImageButton = view.findViewById(R.id.fromBtn)
        var toBtn : ImageButton = view.findViewById(R.id.toBtn)
        var fromHour : TextView = view.findViewById(R.id.from_hour)
        var fromDate : TextView = view.findViewById(R.id.from_date)
        var toHour   : TextView = view.findViewById(R.id.to_hour)
        var toDate   : TextView = view.findViewById(R.id.to_date)
        var applyBtn : ImageButton   = view.findViewById(R.id.applyBtn)
        fromDate.text = getCurrentDate()
        fromHour.text = getCurrentTime()
        //toDate.text   = getCurrentDate()
      //  toHour.text   = getCurrentTime()
        weatherFactory = WeatherViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
            WeatherLocalDataSource(requireContext()), TodayWeatherLocalDataSource(requireContext())
        ),requireContext())
        weatherViewModel = ViewModelProvider(this,weatherFactory).get(WeatherViewModel::class.java)
        applyBtn.setOnClickListener {
           dialog.dismiss()
           scheduleNotification(toDate,toHour)
           weatherViewModel.getHomeWeatherFromRoom()
        }

        builder.setView(view)
        dialog = builder.create()

        notBtnToAdd.setOnClickListener{
            dialog.show()
        }

        fromBtn.setOnClickListener {
            showDatePicker(fromDate, fromHour)
        }

        toBtn.setOnClickListener {
            showDatePicker(toDate, toHour)
        }
        setData (fromDate,fromHour,toDate,toHour)
        lifecycleScope.launch {
            weatherViewModel.weatherFromRoom.observe(viewLifecycleOwner){
                 weather_deatils ->
               // val dateFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())
                val dateFormatSharedPref = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
                val dateFormatRoom = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                Log.i(TAG, "onViewCreated: $weather_deatils")
                date_in_room = weather_deatils.list.get(0).dt_txt
               // dateFormat.parse(date_in_room)
                Log.i(TAG, "onViewCreated: date_in_room : $date_in_room")
                val date_picker = getDateFromSharedPreferences()
                Log.i(TAG, "onViewCreated: date_in_Shared : $date_picker ")
                val date_pickerFormated = convertDateFormat(date_picker)
                Log.i(TAG, "onViewCreated: date_in_Shared after formatted : $date_pickerFormated")
                weather_description = weather_deatils.list.get(0).weather.get(0).description

            }
        }
    }


    private fun setData(fromDate: TextView, fromHour: TextView, toDate: TextView, toHour: TextView) {
        fromD = fromDate.text.toString()
        fromT = fromHour.text.toString()
        toD   = toDate.text.toString()
        toT   = toHour.text.toString()
        Log.i(TAG, "onViewCreated: toD = $toD , toT = $toT")
        notificationData = NotificationData(fromD,fromT,toD,toT,status)
        Log.i(TAG, "onViewCreated: The notificationData is = $notificationData")
    }

    private fun showDatePicker(dateView: TextView, timeView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                Log.i(TAG, "showDatePicker: $selectedDay")
                dateView.text = formatDate(selectedYear, selectedMonth, selectedDay)

                showTimePicker(timeView)
            },
            year, month, day
        )

        // Set minimum date to today
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun scheduleNotification(toDate: TextView, toHour: TextView) {
        val dateFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())
        val date =dateFormat.parse("${toDate.text} ${toHour.text}")
        saveStringToSharedPreferences("${toDate.text}","${toHour.text}")
        Log.i(TAG, "scheduleNotification:${toDate.text} ${toHour.text} ")
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(),NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP,date.time ,pendingIntent)
        Toast.makeText(requireContext(),"Notification Success",Toast.LENGTH_LONG).show()
        Log.i(TAG, "scheduleNotification: Time = ${date}")
        NotificationReceiver.getDescriptionOfWeather(weather_description)
    }

    private fun showTimePicker(timeView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                Log.i(TAG, "showTimePicker: $selectedMinute ")
                val formattedTime = formatTime(selectedHour, selectedMinute)
                timeView.text = formattedTime
            },
            hour, minute, true
        )

        timePickerDialog.show()
    }

    fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        val dateFormat = SimpleDateFormat("d MMM, yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = Date()
        return timeFormat.format(currentTime)
    }

    private fun saveStringToSharedPreferences(date: String , time:String) {
        val sharedPreferences = requireActivity().getSharedPreferences("date_picker",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_PREF, date)
        editor.putString(KEY_PREF2,time)
        editor.apply()
    }

    private fun getDateFromSharedPreferences(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("date_picker",Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_PREF, "") ?: ""
    }
    private fun getTimeFromSharedPreferences(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("date_picker",Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_PREF2,"")?: ""
    }

    fun convertDateFormat(inputDate: String): String {
        // Define input and output date formats
        val inputDateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

        // Parse the input date string
        val date = inputDateFormat.parse(inputDate)

        // Format the parsed date into the desired output format
        return outputDateFormat.format(date)
    }
}