package com.grupo2.sportconnect.data.remote

import com.grupo2.sportconnect.domain.model.AthleteRegistration
import com.grupo2.sportconnect.domain.model.Court
import com.grupo2.sportconnect.domain.model.PasswordRecoveryRequest
import com.grupo2.sportconnect.domain.model.Reservation
import com.grupo2.sportconnect.domain.model.ReservationRequest
import com.grupo2.sportconnect.domain.repository.RepositoryException

/**
 * HTTP boundary to implement with the chosen client (for example Retrofit or Ktor).
 * Implementations should translate transport failures into [RepositoryException]
 * with a safe message suitable for displaying to the user.
 */
interface SportConnectApiService {
    suspend fun getCourts(): List<Court>
    suspend fun getReservations(): List<Reservation>
    suspend fun requestReservation(request: ReservationRequest): Boolean
    suspend fun authenticate(email: String, password: String): Boolean
    suspend fun registerAthlete(registration: AthleteRegistration): Boolean
    suspend fun requestPasswordRecovery(request: PasswordRecoveryRequest)
}
