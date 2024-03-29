package com.example.weatherforecastapplicationkotlin.notification_feature

data class NotificationData (
    val fromDate : String,
    val fromTime : String,
    val toDate   : String,
    val toTime   : String,
    val status   : String
)