package com.grupo2.sportconnect.ui

import java.time.LocalDate

internal enum class SlotAvailability(val label: String) {
    AVAILABLE("Disponible"),
    OCCUPIED("Ocupada / no disponible"),
    RESERVED("Reservada")
}

internal data class TimeSlot(
    val startMinutes: Int,
    val endMinutes: Int,
    val availability: SlotAvailability
)

internal data class ReservationRequest(
    val court: Court,
    val scheduleKey: String,
    val date: LocalDate,
    val slot: TimeSlot
)

internal enum class DemoReservationStatus(val label: String) {
    PENDING_RECEIPT("Pendiente de comprobante"),
    CONFIRMED("Confirmada")
}

internal data class DemoReservation(
    val courtName: String,
    val date: LocalDate,
    val startMinutes: Int,
    val endMinutes: Int,
    val amountPEN: Int,
    val status: DemoReservationStatus
)

internal fun canReserveSlot(
    request: ReservationRequest,
    reservations: List<DemoReservation>
): Boolean {
    val stillAvailable = request.court.scheduleByDay[request.scheduleKey]
        .orEmpty()
        .any { slot ->
            slot.startMinutes == request.slot.startMinutes &&
                slot.endMinutes == request.slot.endMinutes &&
                slot.availability == SlotAvailability.AVAILABLE
        }
    val hasOverlappingReservation = reservations.any { reservation ->
        reservation.date == request.date &&
            reservation.startMinutes < request.slot.endMinutes &&
            request.slot.startMinutes < reservation.endMinutes
    }

    return stillAvailable && !hasOverlappingReservation
}
