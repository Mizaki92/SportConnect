package com.grupo2.sportconnect

import com.grupo2.sportconnect.ui.DemoReservation
import com.grupo2.sportconnect.ui.DemoReservationStatus
import com.grupo2.sportconnect.ui.ReservationRequest
import com.grupo2.sportconnect.ui.SlotAvailability
import com.grupo2.sportconnect.ui.canReserveSlot
import com.grupo2.sportconnect.ui.sampleCourts
import java.time.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservationRulesTest {
    private val today = LocalDate.of(2026, 11, 4)
    private val court = sampleCourts.first()
    private val availableSlot = court.scheduleByDay.getValue("Hoy").first()
    private val request = ReservationRequest(court, "Hoy", today, availableSlot)

    @Test
    fun availableSlotCanBeRequestedWhenThereIsNoConflict() {
        assertTrue(canReserveSlot(request, emptyList()))
    }

    @Test
    fun occupiedOrReservedSlotsCannotBeRequested() {
        val unavailable = court.scheduleByDay.getValue("Hoy")
            .first { it.availability != SlotAvailability.AVAILABLE }
        val unavailableRequest = request.copy(slot = unavailable)

        assertFalse(canReserveSlot(unavailableRequest, emptyList()))
    }

    @Test
    fun activeOverlappingReservationBlocksAnotherRequest() {
        val existingReservation = DemoReservation(
            courtName = "Cancha anterior",
            date = today,
            startMinutes = availableSlot.startMinutes,
            endMinutes = availableSlot.endMinutes,
            amountPEN = 25,
            status = DemoReservationStatus.PENDING_RECEIPT
        )

        assertFalse(canReserveSlot(request, listOf(existingReservation)))
    }

    @Test
    fun reservationOnAnotherDateDoesNotBlockTheSlot() {
        val existingReservation = DemoReservation(
            courtName = court.name,
            date = today.plusDays(1),
            startMinutes = availableSlot.startMinutes,
            endMinutes = availableSlot.endMinutes,
            amountPEN = 40,
            status = DemoReservationStatus.PENDING_RECEIPT
        )

        assertTrue(canReserveSlot(request, listOf(existingReservation)))
    }
}
