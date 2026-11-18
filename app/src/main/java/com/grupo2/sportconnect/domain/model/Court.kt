package com.grupo2.sportconnect.domain.model

import java.time.LocalDate

data class Court(
    val name: String,
    val venue: String,
    val address: String,
    val sport: String,
    val surface: String,
    val features: String,
    val hourlyPricePEN: Int,
    val capacity: Int,
    val conditions: String,
    val scheduleByDay: Map<String, List<TimeSlot>>
) {
    val availableDays: Set<String>
        get() = scheduleByDay
            .filterValues { slots -> slots.any { it.availability == SlotAvailability.AVAILABLE } }
            .keys
}

enum class SlotAvailability(val label: String) {
    AVAILABLE("Disponible"),
    OCCUPIED("Ocupada / no disponible"),
    RESERVED("Reservada")
}

data class TimeSlot(
    val startMinutes: Int,
    val endMinutes: Int,
    val availability: SlotAvailability
)

data class ReservationRequest(
    val court: Court,
    val scheduleKey: String,
    val date: LocalDate,
    val slot: TimeSlot
)

enum class ReservationStatus(val label: String) {
    PENDING_RECEIPT("Pendiente de comprobante"),
    CONFIRMED("Confirmada")
}

data class Reservation(
    val courtName: String,
    val date: LocalDate,
    val startMinutes: Int,
    val endMinutes: Int,
    val amountPEN: Int,
    val status: ReservationStatus
)
