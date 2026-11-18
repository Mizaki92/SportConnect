package com.grupo2.sportconnect.data.repository

import com.grupo2.sportconnect.domain.model.AthleteRegistration
import com.grupo2.sportconnect.domain.model.Court
import com.grupo2.sportconnect.domain.model.PasswordRecoveryRequest
import com.grupo2.sportconnect.domain.model.Reservation
import com.grupo2.sportconnect.domain.model.ReservationRequest
import com.grupo2.sportconnect.domain.model.ReservationStatus
import com.grupo2.sportconnect.domain.model.RecoveryIdentifierType
import com.grupo2.sportconnect.domain.model.SlotAvailability
import com.grupo2.sportconnect.domain.model.TimeSlot
import com.grupo2.sportconnect.domain.repository.AuthRepository
import com.grupo2.sportconnect.domain.repository.CourtRepository
import com.grupo2.sportconnect.domain.usecase.IsReservationAvailableUseCase

class InMemorySportConnectRepository(
    private val isReservationAvailable: IsReservationAvailableUseCase =
        IsReservationAvailableUseCase()
) : CourtRepository, AuthRepository {
    private val lock = Any()
    private val courts = demoCourts()
    private val reservations = mutableListOf<Reservation>()
    private val accounts = mutableMapOf(
        DEMO_EMAIL to StoredAccount(
            document = DEMO_DOCUMENT,
            password = DEMO_PASSWORD
        )
    )
    private val recoveryRequests = mutableListOf<PasswordRecoveryRequest>()

    override suspend fun getCourts(): List<Court> = courts

    override suspend fun getReservations(): List<Reservation> =
        synchronized(lock) { reservations.toList() }

    override suspend fun requestReservation(request: ReservationRequest): Boolean =
        synchronized(lock) {
            if (!isReservationAvailable(request, reservations)) {
                return@synchronized false
            }
            reservations += Reservation(
                courtName = request.court.name,
                date = request.date,
                startMinutes = request.slot.startMinutes,
                endMinutes = request.slot.endMinutes,
                amountPEN = request.court.hourlyPricePEN,
                status = if (request.court.hourlyPricePEN == 0) {
                    ReservationStatus.CONFIRMED
                } else {
                    ReservationStatus.PENDING_RECEIPT
                }
            )
            true
        }

    override suspend fun authenticate(email: String, password: String): Boolean =
        synchronized(lock) {
            accounts[email.trim().lowercase()]?.password == password
        }

    override suspend fun registerAthlete(registration: AthleteRegistration): Boolean =
        synchronized(lock) {
            val normalizedEmail = registration.email.trim().lowercase()
            if (accounts.containsKey(normalizedEmail)) {
                return@synchronized false
            }
            accounts[normalizedEmail] = StoredAccount(
                document = registration.document,
                password = registration.password
            )
            true
        }

    override suspend fun requestPasswordRecovery(request: PasswordRecoveryRequest) {
        synchronized(lock) {
            val accountExists = when (request.type) {
                RecoveryIdentifierType.EMAIL ->
                    accounts.containsKey(request.identifier.lowercase())
                RecoveryIdentifierType.DOCUMENT ->
                    accounts.values.any { it.document == request.identifier }
            }
            if (accountExists) recoveryRequests += request
        }
    }

    private data class StoredAccount(
        val document: String,
        val password: String
    )

    private fun demoCourts(): List<Court> = listOf(
        Court(
            name = "Cancha Sintética N.° 2",
            venue = "Complejo Los Cipreses",
            address = "Calle Jacarandá 340 · San Isidro",
            sport = "Fútbol 7",
            surface = "Césped sintético",
            features = "Iluminación LED",
            hourlyPricePEN = 40,
            capacity = 12,
            conditions = "Reserva de la cancha completa por una franja de 60 minutos.",
            scheduleByDay = mapOf(
                "Hoy" to sampleSlots(
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.OCCUPIED,
                    SlotAvailability.RESERVED
                ),
                "Mañana" to sampleSlots(
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.RESERVED,
                    SlotAvailability.AVAILABLE
                ),
                "Fin de semana" to sampleSlots(
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.OCCUPIED,
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.AVAILABLE
                )
            )
        ),
        Court(
            name = "Losa Multiuso B",
            venue = "Polideportivo Central",
            address = "Av. del Ejército 1120 · Miraflores",
            sport = "Básquet",
            surface = "Piso de resina",
            features = "Focos LED",
            hourlyPricePEN = 0,
            capacity = 10,
            conditions = "Uso gratuito sujeto a las reglas de la sede.",
            scheduleByDay = mapOf(
                "Hoy" to sampleSlots(
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.RESERVED
                ),
                "Fin de semana" to sampleSlots(
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.OCCUPIED,
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.AVAILABLE
                )
            )
        ),
        Court(
            name = "Cancha de tenis 1",
            venue = "Complejo Los Cipreses",
            address = "Calle Jacarandá 340 · San Isidro",
            sport = "Tenis",
            surface = "Superficie dura",
            features = "Iluminación nocturna",
            hourlyPricePEN = 25,
            capacity = 4,
            conditions = "Reserva de la cancha completa por una franja de 60 minutos.",
            scheduleByDay = mapOf(
                "Mañana" to sampleSlots(
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.OCCUPIED,
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.AVAILABLE
                ),
                "Fin de semana" to sampleSlots(
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.AVAILABLE,
                    SlotAvailability.RESERVED,
                    SlotAvailability.AVAILABLE
                )
            )
        )
    )

    private fun sampleSlots(vararg availability: SlotAvailability): List<TimeSlot> =
        availability.mapIndexed { index, status ->
            TimeSlot(
                startMinutes = (16 + index) * 60,
                endMinutes = (17 + index) * 60,
                availability = status
            )
        }

    companion object {
        const val DEMO_EMAIL = "carlos.deportes@ciudad.es"
        const val DEMO_DOCUMENT = "74829103"
        const val DEMO_PASSWORD = "Sport123"
    }
}
