package com.grupo2.sportconnect

import com.grupo2.sportconnect.data.repository.InMemorySportConnectRepository
import com.grupo2.sportconnect.domain.model.AthleteRegistration
import com.grupo2.sportconnect.domain.model.LoginResult
import com.grupo2.sportconnect.domain.model.PasswordRecoveryRequest
import com.grupo2.sportconnect.domain.model.RecoveryIdentifierType
import com.grupo2.sportconnect.domain.model.RecoveryResult
import com.grupo2.sportconnect.domain.model.RegistrationResult
import com.grupo2.sportconnect.domain.usecase.LoginUseCase
import com.grupo2.sportconnect.domain.usecase.RegisterAthleteUseCase
import com.grupo2.sportconnect.domain.usecase.RequestPasswordRecoveryUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthUseCasesTest {
    private val repository = InMemorySportConnectRepository()
    private val login = LoginUseCase(repository)
    private val register = RegisterAthleteUseCase(repository)
    private val recovery = RequestPasswordRecoveryUseCase(repository)

    @Test
    fun demoAccountCanSignInAndRejectsIncorrectPassword() = runBlocking {
        assertEquals(
            LoginResult.AUTHENTICATED,
            login(InMemorySportConnectRepository.DEMO_EMAIL, InMemorySportConnectRepository.DEMO_PASSWORD)
        )
        assertEquals(
            LoginResult.INVALID_CREDENTIALS,
            login(InMemorySportConnectRepository.DEMO_EMAIL, "wrong-password")
        )
    }

    @Test
    fun registrationCreatesAnAccountAndRejectsDuplicateEmail() = runBlocking {
        val registration = validRegistration()

        assertEquals(RegistrationResult.CREATED, register(registration))
        assertEquals(RegistrationResult.EMAIL_ALREADY_REGISTERED, register(registration))
        assertEquals(LoginResult.AUTHENTICATED, login(registration.email, registration.password))
    }

    @Test
    fun recoveryValidatesEmailAndDocumentInputs() = runBlocking {
        assertEquals(
            RecoveryResult.REQUEST_ACCEPTED,
            recovery(PasswordRecoveryRequest(RecoveryIdentifierType.EMAIL, InMemorySportConnectRepository.DEMO_EMAIL))
        )
        assertEquals(
            RecoveryResult.REQUEST_ACCEPTED,
            recovery(PasswordRecoveryRequest(RecoveryIdentifierType.DOCUMENT, InMemorySportConnectRepository.DEMO_DOCUMENT))
        )
        assertEquals(
            RecoveryResult.INVALID_INPUT,
            recovery(PasswordRecoveryRequest(RecoveryIdentifierType.EMAIL, "not-an-email"))
        )
    }

    private fun validRegistration() = AthleteRegistration(
        firstName = "Ana",
        lastName = "Deportista",
        document = "12345678",
        phone = "987654321",
        email = "ana@example.com",
        district = "San Borja",
        password = "Clave123",
        confirmation = "Clave123",
        termsAccepted = true
    )
}
