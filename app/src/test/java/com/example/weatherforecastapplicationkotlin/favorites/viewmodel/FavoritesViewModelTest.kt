package com.example.weatherforecastapplicationkotlin.favorites.viewmodel

import com.example.weatherforecastapplicationkotlin.data.FakeRepository
import com.example.weatherforecastapplicationkotlin.model.Country
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Before
import org.junit.Test
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertEquals

class FavoritesViewModelTest {
    lateinit var viewModel: FavoritesViewModel
    lateinit var repository: FakeRepository

    @Before
    fun setUP()
    {
        repository = FakeRepository()
        viewModel = FavoritesViewModel(repository)
    }

    @Test
    fun insertPlace_InputCountry() = runBlockingTest{
        //Given
        val country = Country("egypt","nasrcity",0.0,0.0)
        //when
        viewModel.insertPlace(country)
        //then
        val result = viewModel.favplaces.first()
        assertThat(result,not(nullValue()))
    }

    @Test
    fun deletePlace_InputCountry() = runBlockingTest {
        //Given
        val country = Country("egypt", "nasrcity", 0.0, 0.0)

        // Insert the country first to ensure it exists
        viewModel.insertPlace(country)

        //when
        viewModel.deletePlace(country)

        //then
        val result = viewModel.favplaces.first()

        // Assert that the deleted country is not present in the list of favorite places
        assertThat(result, not(hasItem(country)))
    }

    @Test
    fun getLocalPlaces_returnAllPlaces () {
        //Given
        val expectedPlaces = listOf(Country("egypt","nasr",0.0,0.0),Country("paris","london",0.0,0.0),Country("america","",0.0,0.0))
        repository.setFakePlaces(expectedPlaces)

        // When
        viewModel.getLocalPlaces()

        // Then
        assertEquals( viewModel.favplaces.value,expectedPlaces)
    }
}