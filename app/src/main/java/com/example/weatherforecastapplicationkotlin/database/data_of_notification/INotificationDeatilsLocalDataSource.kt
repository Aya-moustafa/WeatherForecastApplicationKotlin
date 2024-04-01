package com.example.weatherforecastapplicationkotlin.database.data_of_notification

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.flow.Flow

@Dao
interface INotificationDeatilsLocalDataSource {
    @Query("SELECT * FROM dates_notification")
    fun getAllNotifiData () : Flow<List<NotificationData>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDate (notification: NotificationData)
    @Delete
    suspend fun deleteDate (notification: NotificationData)

    @Query("DELETE FROM dates_notification WHERE toDate =:datetime AND toTime =:hourTime")
    suspend fun deleteNotifiByDate(datetime: String , hourTime : String)
}