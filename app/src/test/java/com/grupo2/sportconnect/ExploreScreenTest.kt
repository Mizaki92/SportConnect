package com.grupo2.sportconnect

import com.grupo2.sportconnect.data.repository.InMemorySportConnectRepository
import com.grupo2.sportconnect.domain.usecase.SearchCourtsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ExploreScreenTest {
    private val courts = runBlocking { InMemorySportConnectRepository().getCourts() }
    private val searchCourts = SearchCourtsUseCase()

    @Test
    fun searchMatchesCourtNameAndVenueWithoutCaseSensitivity() {
        val byName = searchCourts(courts, "SINTÉTICA", "Todos", "Hoy")
        val byVenue = searchCourts(courts, "polideportivo central", "Todos", "Hoy")

        assertEquals(listOf("Cancha Sintética N.° 2"), byName.map { it.name })
        assertEquals(listOf("Losa Multiuso B"), byVenue.map { it.name })
    }

    @Test
    fun sportAndDayFiltersAreAppliedTogether() {
        val results = searchCourts(courts, "", "Tenis", "Hoy")

        assertEquals(emptyList<String>(), results.map { it.name })
    }

    @Test
    fun dayFilterReturnsCourtsAvailableThatDay() {
        val results = searchCourts(courts, "", "Todos", "Mañana")

        assertEquals(
            listOf("Cancha Sintética N.° 2", "Cancha de tenis 1"),
            results.map { it.name }
        )
    }
}
