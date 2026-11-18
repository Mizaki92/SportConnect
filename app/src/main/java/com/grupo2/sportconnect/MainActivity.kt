package com.grupo2.sportconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.grupo2.sportconnect.ui.ExploreScreen
import com.grupo2.sportconnect.ui.theme.SportConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SportConnectTheme {
                ExploreScreen()
            }
        }
    }
}
