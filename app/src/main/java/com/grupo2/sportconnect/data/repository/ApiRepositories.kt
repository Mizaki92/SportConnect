package com.grupo2.sportconnect.data.repository

import com.grupo2.sportconnect.data.remote.SportConnectApiService
import com.grupo2.sportconnect.domain.model.AthleteRegistration
import com.grupo2.sportconnect.domain.model.Court
import com.grupo2.sportconnect.domain.model.PasswordRecoveryRequest
import com.grupo2.sportconnect.domain.model.Reservation
import com.grupo2.sportconnect.domain.model.ReservationRequest
import com.grupo2.sportconnect.domain.repository.AuthRepository
import com.grupo2.sportconnect.domain.repository.CourtRepository

class ApiCourtRepository(
    private val apiService: SportConnectApiService
) : CourtRepository {
    override suspend fun getCourts(): List<Court> = apiService.getCourts()
    override suspend fun getReservations(): List<Reservation> = apiService.getReservations()
    override suspend fun requestReservation(request: ReservationRequest): Boolean =
        apiService.requestReservation(request)
}

class ApiAuthRepository(
    private val apiService: SportConnectApiService
) : AuthRepository {
    override suspend fun authenticate(email: String, password: String): Boolean =
        apiService.authenticate(email, password)

    override suspend fun registerAthlete(registration: AthleteRegistration): Boolean =
        apiService.registerAthlete(registration)

    override suspend fun requestPasswordRecovery(request: PasswordRecoveryRequest) {
        apiService.requestPasswordRecovery(request)
    }
}
