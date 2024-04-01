package com.example.weatherforecastapplicationkotlin.notification_feature.NotifViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapplicationkotlin.model.repository.IWeatherRepository
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(private var _repo : IWeatherRepository) : ViewModel() {
    val _notificationIdToDelete = MutableLiveData<Int>()
    private var _notificationsDate : MutableStateFlow<List<NotificationData>> = MutableStateFlow<List<NotificationData>>(emptyList())
    val notificationsDate  : StateFlow<List<NotificationData>> = _notificationsDate
    val notificationIdToDelete: LiveData<Int>
        get() = _notificationIdToDelete

    init {
        getNotifiDetails()
    }

    fun setNotificationIdToDelete(notificationId: Int) {
        _notificationIdToDelete.value = notificationId
    }
    fun getNotifiDetails () {
        viewModelScope.launch(Dispatchers.IO){
            _repo.getAllNotificationsDate().collect{
                    dates -> _notificationsDate.emit(dates)
            }
        }
    }

    fun deleteNotifiByDate (datetime: String, hourTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteNotifiByDate(datetime , hourTime)
        }
    }

    fun insertNewDate (notificationData: NotificationData){
        viewModelScope.launch(Dispatchers.IO){
            _repo.insertDate(notificationData)
        }
    }

    fun deleteOldDate (notificationData: NotificationData){
        viewModelScope.launch(Dispatchers.IO){
            _repo.deleteDate(notificationData)
            getNotifiDetails()
        }
    }

}