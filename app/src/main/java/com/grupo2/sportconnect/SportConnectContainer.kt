package com.grupo2.sportconnect

import com.grupo2.sportconnect.data.remote.SportConnectApiService
import com.grupo2.sportconnect.data.repository.ApiAuthRepository
import com.grupo2.sportconnect.data.repository.ApiCourtRepository
import com.grupo2.sportconnect.data.repository.InMemorySportConnectRepository
import com.grupo2.sportconnect.domain.repository.AuthRepository
import com.grupo2.sportconnect.domain.repository.CourtRepository
import com.grupo2.sportconnect.domain.usecase.GetCourtsUseCase
import com.grupo2.sportconnect.domain.usecase.GetReservationsUseCase
import com.grupo2.sportconnect.domain.usecase.IsReservationAvailableUseCase
import com.grupo2.sportconnect.domain.usecase.LoginUseCase
import com.grupo2.sportconnect.domain.usecase.RegisterAthleteUseCase
import com.grupo2.sportconnect.domain.usecase.RequestPasswordRecoveryUseCase
import com.grupo2.sportconnect.domain.usecase.RequestReservationUseCase
import com.grupo2.sportconnect.domain.usecase.SearchCourtsUseCase

class SportConnectContainer private constructor(
    val demoCredentialsHint: String?,
    val getCourts: GetCourtsUseCase,
    val getReservations: GetReservationsUseCase,
    val searchCourts: SearchCourtsUseCase,
    val isReservationAvailable: IsReservationAvailableUseCase,
    val requestReservation: RequestReservationUseCase,
    val login: LoginUseCase,
    val registerAthlete: RegisterAthleteUseCase,
    val requestPasswordRecovery: RequestPasswordRecoveryUseCase
) {
    companion object {
        fun createDemo(): SportConnectContainer {
            val repository = InMemorySportConnectRepository()
            return create(
                courtRepository = repository,
                authRepository = repository,
                demoCredentialsHint = "${InMemorySportConnectRepository.DEMO_EMAIL} · " +
                    InMemorySportConnectRepository.DEMO_PASSWORD
            )
        }

        fun createWithApi(apiService: SportConnectApiService): SportConnectContainer =
            create(
                courtRepository = ApiCourtRepository(apiService),
                authRepository = ApiAuthRepository(apiService),
                demoCredentialsHint = null
            )

        private fun create(
            courtRepository: CourtRepository,
            authRepository: AuthRepository,
            demoCredentialsHint: String?
        ): SportConnectContainer {
            return SportConnectContainer(
                demoCredentialsHint = demoCredentialsHint,
                getCourts = GetCourtsUseCase(courtRepository),
                getReservations = GetReservationsUseCase(courtRepository),
                searchCourts = SearchCourtsUseCase(),
                isReservationAvailable = IsReservationAvailableUseCase(),
                requestReservation = RequestReservationUseCase(courtRepository),
                login = LoginUseCase(authRepository),
                registerAthlete = RegisterAthleteUseCase(authRepository),
                requestPasswordRecovery = RequestPasswordRecoveryUseCase(authRepository)
            )
        }
    }
}
