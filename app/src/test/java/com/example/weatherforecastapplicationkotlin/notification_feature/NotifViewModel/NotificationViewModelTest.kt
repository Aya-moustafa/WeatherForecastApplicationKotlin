package com.example.weatherforecastapplicationkotlin.notification_feature.NotifViewModel

import com.example.weatherforecastapplicationkotlin.data.FakeRepository
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

class NotificationViewModelTest {
    lateinit var viewModel: NotificationViewModel
    lateinit var repository: FakeRepository

    @Before
    fun setUP()
    {
        repository = FakeRepository()
        viewModel = NotificationViewModel(repository)
    }

    @Test
    fun insertNewDate_InputNotificationDate() = runBlockingTest {
        //Given
        val notificationData = NotificationData("12/22/23", "12/23/23", "", "", "")

        // When
        viewModel.insertNewDate(notificationData)

        // Then
        val result = repository.getAllNotificationsDate().first()
        assertEquals(listOf(notificationData), result)   }

    @Test
    fun deleteNewDate_InputNotificationDate() = runBlockingTest(){
        //Given
        val notificationData = NotificationData("12/22/23","12/23/23","","","")
        //when
        viewModel.insertNewDate(notificationData)
        viewModel.deleteOldDate(notificationData)
        // Given
        val result = repository.getAllNotificationsDate().first()
        assertEquals(emptyList<NotificationData>(), result)

    }

    @Test
    fun getNotifiDetails_returnAllNotificationsDate () = runBlockingTest {
        //Given
        val fakeNotificationData = listOf(
            NotificationData(fromDate = "12/22/23", fromTime = "12/23/23", toDate = "", toTime = "", status = "")
        )
        repository.setFakeNotification(fakeNotificationData)

        //when
        viewModel.getNotifiDetails()

        //then
        val emittedValue = viewModel.notificationsDate.first()
        assertEquals(fakeNotificationData, emittedValue)

    }
}