package com.grupo2.sportconnect

import com.grupo2.sportconnect.ui.filterCourts
import com.grupo2.sportconnect.ui.sampleCourts
import org.junit.Assert.assertEquals
import org.junit.Test

class ExploreScreenTest {
    @Test
    fun searchMatchesCourtNameAndVenueWithoutCaseSensitivity() {
        val byName = filterCourts(sampleCourts, "SINTÉTICA", "Todos", "Hoy")
        val byVenue = filterCourts(sampleCourts, "polideportivo central", "Todos", "Hoy")

        assertEquals(listOf("Cancha Sintética N.° 2"), byName.map { it.name })
        assertEquals(listOf("Losa Multiuso B"), byVenue.map { it.name })
    }

    @Test
    fun sportAndDayFiltersAreAppliedTogether() {
        val results = filterCourts(sampleCourts, "", "Tenis", "Hoy")

        assertEquals(emptyList<String>(), results.map { it.name })
    }

    @Test
    fun dayFilterReturnsCourtsAvailableThatDay() {
        val results = filterCourts(sampleCourts, "", "Todos", "Mañana")

        assertEquals(
            listOf("Cancha Sintética N.° 2", "Cancha de tenis 1"),
            results.map { it.name }
        )
    }
}
