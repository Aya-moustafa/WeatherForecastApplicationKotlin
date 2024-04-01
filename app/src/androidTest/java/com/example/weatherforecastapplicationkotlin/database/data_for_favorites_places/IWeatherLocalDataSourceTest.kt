package com.example.weatherforecastapplicationkotlin.database.data_for_favorites_places

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.weatherforecastapplicationkotlin.model.Country
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class IWeatherLocalDataSourceTest {
    lateinit var database: HomeDataBase
    lateinit var dao :IWeatherLocalDataSource

    @get:Rule
    val rule = InstantTaskExecutorRule()
    @Before
    fun setUp(){
        database = Room.inMemoryDatabaseBuilder(getApplicationContext(),HomeDataBase::class.java).build()
        dao = database.getFavoritesDao()
    }
    @After
    fun tearDown(){
        database.close()
    }


    @Test
    fun testinsertandgetPlace_InputPlace () = runTest {
        //Given
        val place = Country("Egypt" , "cairo" ,0.0,0.0)
        dao.insertPlace(place)
        //when
        dao.getFavPlaces().first { favorites ->
            assertThat(favorites, `is`(not(nullValue())))
            assertThat(favorites[0].longtuide, `is`(place.longtuide))
            true
        }
    }

    @Test
    fun testdeletePlace_InputPlaceTodelete() = runTest {
        //Given
        val place1 = Country("Egypt" , "cairo" ,0.0,0.0)
        val place2 = Country("Egypt" , "cairo" ,0.0,0.0)

        //when
        dao.insertPlace(place1)
        dao.insertPlace(place2)
        dao.deletePlace(place1)

        //then
        val favorites = dao.getFavPlaces().first()
        assertThat(favorites , `is`(not(nullValue())))
    }


}