package com.example.weatherforecastapplicationkotlin.model.repository

import com.example.weatherforecastapplicationkotlin.data.FakeLocalDataSource
import com.example.weatherforecastapplicationkotlin.data.FakeRemoteDataSource
import com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places.ILocalDataSource
import com.example.weatherforecastapplicationkotlin.model.Country
import com.example.weatherforecastapplicationkotlin.network.IWeatherRemoteDataSource
import com.example.weatherforecastapplicationkotlin.notification_feature.model.NotificationData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals


class WeatherRepositoryTest {

    lateinit var fakeLocalDataSource:FakeLocalDataSource
    lateinit var fakeRemoteDataSource:FakeRemoteDataSource
    lateinit var repository: IWeatherRepository

    val fav_place1 = Country("Egypt" , "NasrCity" , 30.00 , 31.21)
    val fav_place2 = Country("Paris" , "City1" , 32.00 , 33.21)
    val fav_place3 = Country("America" , "City2" , 20.00 , 21.21)

    val alert1 = NotificationData("2024/03/28","02:30" ,"2024/03/28" , "02:35" , " ")
    val alert2 = NotificationData("2024/03/31","02:30" ,"2024/03/31" , "02:35" , " ")


    val alerts = mutableListOf<NotificationData>(alert1,alert2)
    val favorites = mutableListOf<Country>(fav_place2 , fav_place1 , fav_place3)

    @Before
    fun setUp() {
        fakeLocalDataSource = FakeLocalDataSource()
        fakeRemoteDataSource = FakeRemoteDataSource()
        repository = WeatherRepository.getInstance(fakeRemoteDataSource,fakeLocalDataSource)
    }

    @Test
    fun testinsertNewPlaceToFavorites_InputPlace () = runBlockingTest{
          //when
        repository.insertNewPlaceToFavorites(fav_place1)
        val favorites = repository.viewAllFavorites().first()
        assertEquals(listOf(fav_place1), favorites)
    }

    @Test
    fun testviewAllFavorites_returnAllFavorites () = runBlockingTest{
        //when
        repository.insertNewPlaceToFavorites(fav_place1)
        repository.deletePlaceFromFavorites(fav_place1)
        //then
        val favorites = repository.viewAllFavorites().first()
        assertEquals(emptyList<Country>() , favorites)
    }

    @Test
    fun testgetWeatherForecast_returnWeatherFromApi () {
        val lat = 0.0
        val lon = 0.0
        val apiKey = "api_key"
        val units = "metric"
        val lan = "en"
        runBlockingTest {
            val response = fakeRemoteDataSource.getWeatherForecastOverNetwork(lat, lon, apiKey, units, lan)
            assertEquals(true, response.isSuccessful)
            response.body()?.let { weatherForecast ->
                assertEquals(fakeRemoteDataSource.weather ,weatherForecast)
            }
        }
    }

    @Test
    fun testinsertDate_InputAlertDate () {
        runBlockingTest {
            //when
            repository.insertDate(alert1)
            val notifications = fakeLocalDataSource.getAllNotifiData().first()
            //Then
            assertEquals(listOf(alert1), notifications)
        }
    }

    @Test
    fun testdeleteDate_InputAlertDate () {
        runTest {
            //when
            repository.insertDate(alert1)
            repository.insertDate(alert2)
            repository.deleteDate(alert1)

            //then
            val notifications = fakeLocalDataSource.getAllNotifiData().first()
            assertEquals(listOf(alert2),notifications)
        }
    }

    @Test
    fun testgetAllNotificationsDate_returnAllAlretsDate () {
        runTest{
            //when
            repository.insertDate(alert1)
            repository.insertDate(alert2)

            //then
            val notifications = repository.getAllNotificationsDate().first()
            assertEquals(listOf(alert1,alert2) , notifications)

        }
    }

}