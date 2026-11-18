package com.grupo2.sportconnect.domain.usecase

import com.grupo2.sportconnect.domain.model.AthleteRegistration
import com.grupo2.sportconnect.domain.model.LoginResult
import com.grupo2.sportconnect.domain.model.PasswordRecoveryRequest
import com.grupo2.sportconnect.domain.model.RecoveryIdentifierType
import com.grupo2.sportconnect.domain.model.RecoveryResult
import com.grupo2.sportconnect.domain.model.RegistrationResult
import com.grupo2.sportconnect.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): LoginResult {
        if (!email.isValidEmail() || password.isBlank()) return LoginResult.INVALID_INPUT
        return if (repository.authenticate(email.trim(), password)) {
            LoginResult.AUTHENTICATED
        } else {
            LoginResult.INVALID_CREDENTIALS
        }
    }
}

class RegisterAthleteUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(registration: AthleteRegistration): RegistrationResult {
        if (!registration.isValid()) return RegistrationResult.INVALID_DATA
        return if (repository.registerAthlete(registration)) {
            RegistrationResult.CREATED
        } else {
            RegistrationResult.EMAIL_ALREADY_REGISTERED
        }
    }
}

class RequestPasswordRecoveryUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(request: PasswordRecoveryRequest): RecoveryResult {
        val identifier = request.identifier.trim()
        val valid = when (request.type) {
            RecoveryIdentifierType.EMAIL -> identifier.isValidEmail()
            RecoveryIdentifierType.DOCUMENT -> identifier.length >= 8 && identifier.all(Char::isDigit)
        }
        if (!valid) return RecoveryResult.INVALID_INPUT

        repository.requestPasswordRecovery(request.copy(identifier = identifier))
        return RecoveryResult.REQUEST_ACCEPTED
    }
}

private fun AthleteRegistration.isValid(): Boolean =
    firstName.isNotBlank() &&
        lastName.isNotBlank() &&
        document.length >= 8 &&
        document.all(Char::isDigit) &&
        phone.isNotBlank() &&
        email.isValidEmail() &&
        district.isNotBlank() &&
        password.length >= 8 &&
        password.any(Char::isDigit) &&
        confirmation == password &&
        termsAccepted

private fun String.isValidEmail(): Boolean =
    Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(trim())
