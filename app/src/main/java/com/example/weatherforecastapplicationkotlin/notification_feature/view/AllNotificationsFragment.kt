package com.example.weatherforecastapplicationkotlin.notification_feature.view

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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherforecastapplicationkotlin.MainActivity.API_KEY
import com.example.weatherforecastapplicationkotlin.R
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.LocalDataSource
import com.example.weatherforecastapplicationkotlin.home_page.model.WeatherForeCastState
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModel
import com.example.weatherforecastapplicationkotlin.home_page.view_model.WeatherViewModelFactory
import com.example.weatherforecastapplicationkotlin.model.AllDaos
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.model.repository.WeatherRepository
import com.example.weatherforecastapplicationkotlin.network.WeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.notification_feature.NotifViewModel.NotificationViewModel
import com.example.weatherforecastapplicationkotlin.notification_feature.NotifViewModel.NotificationViewModelFactory
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewMode
import com.example.weatherforecastapplicationkotlin.setting.viewmodel.SettingViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AllNotificationsFragment : Fragment(){
    private final var TAG : String  = "AllNotificationsFragment"
    private val KEY_PREF = "date_to_notification"
    private val KEY_PREF2 = "time_to_notification"
    private val KEY_FD = "From_Date"
    private val KEY_FT = "From_Time"
    lateinit var adapter: NotificationListAdapter
    lateinit var notifiRecycler : RecyclerView
    lateinit var notBtnToAdd : FloatingActionButton
    lateinit var dialog: AlertDialog
    lateinit var notificationData: NotificationData
    lateinit  var weatherViewModel: WeatherViewModel
    lateinit var  weatherFactory: WeatherViewModelFactory
    lateinit var  notfViewModel : NotificationViewModel
    lateinit var  notifFactory  : NotificationViewModelFactory
    lateinit  var settingViewModel: SettingViewMode
    lateinit var  sharedFactory: SettingViewModelFactory
     var fromD : String = ""
     var fromT : String = ""
     var toD : String = ""
     var toT : String = ""
     var status : String = "notification"
    var weather_description : String = ""
    var date_in_room : String = ""
    var fromNotification : Boolean = true
    private var notificationIdCounter = 0
    lateinit var country_details : Country
    var latitude : Double = 0.0
    var longtuide : Double = 0.0
    var description : String = " "
    var cityName : String = " "
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
        arguments?.let { args ->
            val country = args.getSerializable("country_notification") as? Country
            if (country != null) {
                country_details = country
                latitude = country.latitude
                longtuide = country.longtuide
                Log.i(TAG, "onViewCreated: The Returned country from the Setting fragment is be : ${country_details}")
            }
        }
        sharedFactory = SettingViewModelFactory(requireActivity().application)
        settingViewModel = ViewModelProvider(this,sharedFactory).get(SettingViewMode::class.java)
        fromDate.text = getCurrentDate()
        fromHour.text = getCurrentTime()
        //toDate.text   = getCurrentDate()
      //  toHour.text   = getCurrentTime()
        weatherFactory = WeatherViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance() , LocalDataSource(AllDaos(requireContext()))),requireContext())
        weatherViewModel = ViewModelProvider(this,weatherFactory).get(WeatherViewModel::class.java)
        notifFactory = NotificationViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance() , LocalDataSource(AllDaos(requireContext()))))
        notfViewModel = ViewModelProvider(this@AllNotificationsFragment , notifFactory).get(NotificationViewModel::class.java)
        notfViewModel.getNotifiDetails()
        weatherViewModel.getWeatherForecast(latitude,longtuide, API_KEY,"standard","en",true)
        applyBtn.setOnClickListener {
           dialog.dismiss()
           scheduleNotification(toDate,toHour,fromDate,fromHour)
           weatherViewModel.getHomeWeatherFromRoom()
            val navController = findNavController()
            val bundle = Bundle().apply {
                putBoolean("fromNotification", true) // Pass your boolean flag here
            }
            navController.navigate(R.id.searchOnMapFragment, bundle)
        }
        adapter = NotificationListAdapter(requireContext())
        val layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        notifiRecycler.adapter = adapter
        notifiRecycler.layoutManager = layoutManager

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
       /* lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weatherFromRoom.collect{
                        weather_deatils ->
                    when(weather_deatils){
                        is WeatherForeCastState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            notifiRecycler.visibility= View.GONE
                        }is WeatherForeCastState.Success -> {
                            progressBar.visibility = View.GONE
                            notifiRecycler.visibility= View.VISIBLE
                            val dateFormatSharedPref = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
                            val dateFormatRoom = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                            Log.i(TAG, "onViewCreated: $weather_deatils")
                            date_in_room = weather_deatils.data.list.get(0).dt_txt
                            // dateFormat.parse(date_in_room)
                            Log.i(TAG, "onViewCreated: date_in_room : $date_in_room")
                            val date_picker = getDateFromSharedPreferences()
                            Log.i(TAG, "onViewCreated: date_in_Shared : $date_picker ")
                            val date_pickerFormated = convertDateFormat(date_picker)
                            Log.i(TAG, "onViewCreated: date_in_Shared after formatted : $date_pickerFormated")
                            weather_description = weather_deatils.data.list.get(0).weather.get(0).description
                            setData (fromDate,fromHour,toDate,toHour)
                        }is WeatherForeCastState.Failure -> {
                            notifiRecycler.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Error loading data!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }*/
        setData (fromDate,fromHour,toDate,toHour)
        lifecycleScope.launch {
            notfViewModel.notificationsDate.collect{
                dates ->
                Log.i(TAG, "onViewCreated: AllDatesInRoom : dates")
                adapter.submitList(dates)
            }
        }
        notfViewModel.notificationIdToDelete.observe(viewLifecycleOwner) { notificationId ->
            notificationId?.let {
                deleteNotification(notificationId)
            }
        }
        lifecycleScope.launch {
            settingViewModel.settingsFlow.collectLatest {
                    setting ->
                Log.i(TAG, "onViewCreated: setting  ")
            }
        }
        lifecycleScope.launch {
            weatherViewModel.weatherForecast.collect{
                weather ->
                Log.i(TAG, "onViewCreated: The Returned API $weather")
                Log.i(TAG, "onViewCreated: The Returned API ${getDatePart(weather.list.get(0).dt_txt)}")
                var dateFromApi = getDatePart(weather.list.get(0).dt_txt)
                var datePicker = converttDateFormat(getDateFromSharedPreferences())
                cityName = weather.city.country
                Log.i(TAG, "onViewCreated: The Returned API ${dateFromApi} ,,,,, $datePicker")
                if(dateFromApi == datePicker) {
                   description = weather.list.get(0).weather.get(0).description
                }
            }
        }
    }
    private fun setData(fromDate: TextView, fromHour: TextView, toDate: TextView, toHour: TextView) {
        fromD = getFDateFromSharedPreferences()
        fromT = getFTimeFromSharedPreferences()
        toD = getDateFromSharedPreferences()
        toT = getTimeFromSharedPreferences()
        Log.i(TAG, "onViewCreated: toD = $toD , toT = $toT")
        notificationData = NotificationData(fromD, fromT, toD, toT, status)
        notfViewModel.insertNewDate(notificationData)
        //   weatherViewModel.getNotifiDetails()
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

    private fun scheduleNotification(toDate: TextView, toHour: TextView,fromDate: TextView , fromHour: TextView) {
        val dateFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())
        val date =dateFormat.parse("${toDate.text} ${toHour.text}")
        val timeDate = toDate.text
        val timeHour = toHour.text
        saveStringToSharedPreferences("${toDate.text}","${toHour.text}" , "${fromDate.text}" ,"${fromHour.text}" )
        Log.i(TAG, "scheduleNotification:${toDate.text} ${toHour.text} ")
        val notificationId = generateNotificationId()
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        intent.putExtra("description", description)
        intent.putExtra("cityname", cityName)
        intent.putExtra("date",timeDate)
        intent.putExtra("hour",timeHour)
        intent.putExtra("notificationId", notificationId)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        Log.i(TAG, "scheduleNotification:${date.time} ")
        alarmManager.set(AlarmManager.RTC_WAKEUP,date.time ,pendingIntent)
        Toast.makeText(requireContext(),"Notification Success",Toast.LENGTH_LONG).show()
        Log.i(TAG, "scheduleNotification: Time = ${date}")
      //  NotificationReceiver.getDescriptionOfWeather(weather_description)
     //   weatherViewModel.deleteOldDate(notificationData)
    }

    private fun generateNotificationId(): Int {
        return notificationIdCounter++
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

    private fun saveStringToSharedPreferences(date: String , time:String, fromDate :String , fromTime : String) {
        val sharedPreferences = requireActivity().getSharedPreferences("date_picker",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_PREF, date)
        editor.putString(KEY_PREF2,time)
        editor.putString(KEY_FD,fromDate)
        editor.putString(KEY_FT,fromTime)

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
    private fun getFDateFromSharedPreferences(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("date_picker",Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_FD, "") ?: ""
    }
    private fun getFTimeFromSharedPreferences(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("date_picker",Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_FT,"")?: ""
    }
    private fun scheduleWorkManager(notificationId: Int) {
        val inputData = workDataOf("notificationId" to notificationId)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val deleteNotificationWorkRequest =
            OneTimeWorkRequestBuilder<DeleteNotitificationWorker>()
                .setInputData(inputData)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(requireContext()).enqueue(deleteNotificationWorkRequest)
    }


    fun deleteNotification(notificationId: Int) {
        val notificationData = adapter.getItem(notificationId)
        if (notificationData != null) {
            notfViewModel.deleteOldDate(notificationData)
        }
    }

    fun getDatePart(dateTimeString: String): String {
        val parts = dateTimeString.split(" ")
        return parts[0]
    }

    fun converttDateFormat(inputDate: String): String {
        if (inputDate.isNotBlank()) {
            val inputFormat = SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } else {
            return ""
        }
    }
}