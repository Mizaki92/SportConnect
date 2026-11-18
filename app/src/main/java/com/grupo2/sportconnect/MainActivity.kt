package com.grupo2.sportconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.grupo2.sportconnect.ui.LoginScreen
import com.grupo2.sportconnect.ui.RecoveryScreen
import com.grupo2.sportconnect.ui.RegisterScreen
import com.grupo2.sportconnect.ui.ExploreScreen
import com.grupo2.sportconnect.ui.theme.SportConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SportConnectTheme {
                val container = remember { SportConnectContainer.createDemo() }
                var destination by rememberSaveable { mutableStateOf("explore") }
                when (destination) {
                    "login" -> LoginScreen(
                        demoCredentialsHint = container.demoCredentialsHint,
                        onLogin = { email, password -> container.login(email, password) },
                        onLoginSuccess = { destination = "explore" },
                        onRegister = { destination = "register" },
                        onRecoverPassword = { destination = "recovery" },
                        onExploreAsGuest = { destination = "explore" }
                    )
                    "register" -> RegisterScreen(
                        onRegister = { registration -> container.registerAthlete(registration) },
                        onBackToLogin = { destination = "login" }
                    )
                    "recovery" -> RecoveryScreen(
                        onRequestRecovery = { request -> container.requestPasswordRecovery(request) },
                        onBackToLogin = { destination = "login" }
                    )
                    else -> ExploreScreen(
                        onLoginClick = { destination = "login" },
                        getCourts = container.getCourts,
                        getReservations = container.getReservations,
                        searchCourts = container.searchCourts,
                        isReservationAvailable = container.isReservationAvailable,
                        requestReservation = container.requestReservation
                    )
                }
            }
        }
    }
}
