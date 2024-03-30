package com.example.weatherforecastapplicationkotlin.notification_feature.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "dates_notification")
data class NotificationData (
    @NotNull
    val fromDate : String,
    @NotNull
    val fromTime : String,
    val toDate   : String,
    @PrimaryKey
    @NotNull
    val toTime   : String,
    val status   : String
)