package dev.xero.tomabar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.xero.tomabar.presentation.screens.home.HomeScreen
import dev.xero.tomabar.presentation.screens.onboarding.OnboardingScreen
import dev.xero.tomabar.presentation.theme.TomaBarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            TomaBarTheme {
                val app = applicationContext as TomaBarApplication
                val savedIp by app.container.connectionPrefs.baseUrl
                    .collectAsStateWithLifecycle(initialValue = UNRESOLVED)

                Scaffold(containerColor = MaterialTheme.colorScheme.surface) { innerPadding ->
                    val mod = Modifier.padding(innerPadding)
                    when (savedIp) {
                        UNRESOLVED -> Unit
                        null -> OnboardingScreen(
                            onConnected = { /* prefs now has an IP; the flow below reacts */ },
                            modifier = mod
                        )
                        else -> HomeScreen(
                            onNeedsReconnect = { /* repo cleared the IP; flow reacts */ },
                            modifier = mod
                        )
                    }
                }
            }
        }
    }
}

private const val UNRESOLVED = "::unresolved::"