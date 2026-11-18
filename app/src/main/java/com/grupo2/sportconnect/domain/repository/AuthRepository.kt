package com.grupo2.sportconnect.domain.repository

import com.grupo2.sportconnect.domain.model.AthleteRegistration
import com.grupo2.sportconnect.domain.model.PasswordRecoveryRequest

interface AuthRepository {
    suspend fun authenticate(email: String, password: String): Boolean
    suspend fun registerAthlete(registration: AthleteRegistration): Boolean
    suspend fun requestPasswordRecovery(request: PasswordRecoveryRequest)
}
