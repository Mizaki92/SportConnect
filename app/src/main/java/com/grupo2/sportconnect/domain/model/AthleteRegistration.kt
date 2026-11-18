package com.grupo2.sportconnect.domain.model

data class AthleteRegistration(
    val firstName: String,
    val lastName: String,
    val document: String,
    val phone: String,
    val email: String,
    val district: String,
    val password: String,
    val confirmation: String,
    val termsAccepted: Boolean
)

enum class RecoveryIdentifierType {
    EMAIL,
    DOCUMENT
}

data class PasswordRecoveryRequest(
    val type: RecoveryIdentifierType,
    val identifier: String
)

enum class LoginResult {
    AUTHENTICATED,
    INVALID_INPUT,
    INVALID_CREDENTIALS
}

enum class RegistrationResult {
    CREATED,
    INVALID_DATA,
    EMAIL_ALREADY_REGISTERED
}

enum class RecoveryResult {
    REQUEST_ACCEPTED,
    INVALID_INPUT
}
