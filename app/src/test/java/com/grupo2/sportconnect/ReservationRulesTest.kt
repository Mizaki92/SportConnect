package com.grupo2.sportconnect

import com.grupo2.sportconnect.data.repository.InMemorySportConnectRepository
import com.grupo2.sportconnect.domain.model.Reservation
import com.grupo2.sportconnect.domain.model.ReservationStatus
import com.grupo2.sportconnect.domain.model.ReservationRequest
import com.grupo2.sportconnect.domain.model.SlotAvailability
import com.grupo2.sportconnect.domain.usecase.IsReservationAvailableUseCase
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservationRulesTest {
    private val today = LocalDate.of(2026, 11, 4)
    private val court = runBlocking { InMemorySportConnectRepository().getCourts().first() }
    private val availableSlot = court.scheduleByDay.getValue("Hoy").first()
    private val request = ReservationRequest(court, "Hoy", today, availableSlot)
    private val isReservationAvailable = IsReservationAvailableUseCase()

    @Test
    fun availableSlotCanBeRequestedWhenThereIsNoConflict() {
        assertTrue(isReservationAvailable(request, emptyList()))
    }

    @Test
    fun occupiedOrReservedSlotsCannotBeRequested() {
        val unavailable = court.scheduleByDay.getValue("Hoy")
            .first { it.availability != SlotAvailability.AVAILABLE }
        val unavailableRequest = request.copy(slot = unavailable)

        assertFalse(isReservationAvailable(unavailableRequest, emptyList()))
    }

    @Test
    fun activeOverlappingReservationBlocksAnotherRequest() {
        val existingReservation = Reservation(
            courtName = "Cancha anterior",
            date = today,
            startMinutes = availableSlot.startMinutes,
            endMinutes = availableSlot.endMinutes,
            amountPEN = 25,
            status = ReservationStatus.PENDING_RECEIPT
        )

        assertFalse(isReservationAvailable(request, listOf(existingReservation)))
    }

    @Test
    fun reservationOnAnotherDateDoesNotBlockTheSlot() {
        val existingReservation = Reservation(
            courtName = court.name,
            date = today.plusDays(1),
            startMinutes = availableSlot.startMinutes,
            endMinutes = availableSlot.endMinutes,
            amountPEN = 40,
            status = ReservationStatus.PENDING_RECEIPT
        )

        assertTrue(isReservationAvailable(request, listOf(existingReservation)))
    }

    @Test
    fun inMemoryRepositoryPersistsReservationAndRejectsDuplicateRequest() = runBlocking {
        val repository = InMemorySportConnectRepository()
        val court = repository.getCourts().first()
        val slot = court.scheduleByDay.getValue("Hoy").first()
        val reservationRequest = ReservationRequest(court, "Hoy", today, slot)

        assertTrue(repository.requestReservation(reservationRequest))
        assertFalse(repository.requestReservation(reservationRequest))
        assertEquals(1, repository.getReservations().size)
    }
}
