package com.grupo2.sportconnect.domain.usecase

import com.grupo2.sportconnect.domain.model.Court
import com.grupo2.sportconnect.domain.model.ReservationRequest
import com.grupo2.sportconnect.domain.model.Reservation
import com.grupo2.sportconnect.domain.model.SlotAvailability
import com.grupo2.sportconnect.domain.repository.CourtRepository

class GetCourtsUseCase(private val repository: CourtRepository) {
    suspend operator fun invoke(): List<Court> = repository.getCourts()
}

class GetReservationsUseCase(private val repository: CourtRepository) {
    suspend operator fun invoke(): List<Reservation> = repository.getReservations()
}

class SearchCourtsUseCase {
    operator fun invoke(
        courts: List<Court>,
        query: String,
        sport: String,
        day: String
    ): List<Court> {
        val normalizedQuery = query.trim()
        return courts.filter { court ->
            val matchesQuery = normalizedQuery.isEmpty() ||
                listOf(court.name, court.venue, court.address, court.sport)
                    .any { it.contains(normalizedQuery, ignoreCase = true) }
            val matchesSport = sport == "Todos" || court.sport == sport
            val matchesDay = court.availableDays.contains(day)
            matchesQuery && matchesSport && matchesDay
        }
    }
}

class IsReservationAvailableUseCase {
    operator fun invoke(
        request: ReservationRequest,
        reservations: List<Reservation>
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
}

class RequestReservationUseCase(private val repository: CourtRepository) {
    suspend operator fun invoke(request: ReservationRequest): Boolean =
        repository.requestReservation(request)
}
