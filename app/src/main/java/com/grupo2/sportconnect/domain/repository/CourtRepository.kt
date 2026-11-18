package com.grupo2.sportconnect.domain.repository

import com.grupo2.sportconnect.domain.model.Court
import com.grupo2.sportconnect.domain.model.Reservation
import com.grupo2.sportconnect.domain.model.ReservationRequest

interface CourtRepository {
    suspend fun getCourts(): List<Court>
    suspend fun getReservations(): List<Reservation>
    suspend fun requestReservation(request: ReservationRequest): Boolean
}
